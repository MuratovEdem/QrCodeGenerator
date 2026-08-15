package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.RegistrationUserRequestDto;
import controlm.qrcodegenerator.dto.response.RegistrationUserResponseDto;
import controlm.qrcodegenerator.dto.response.UserDashboardResponseDto;
import controlm.qrcodegenerator.dto.response.UserResponseDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.AuditLog;
import controlm.qrcodegenerator.model.Role;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.AuditLogRepository;
import controlm.qrcodegenerator.repository.UserRepository;
import controlm.qrcodegenerator.utils.RandomUtils;
import controlm.qrcodegenerator.utils.TransliterateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransliterateUtils transliterateUtils;
    private final RandomUtils randomUtils;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public RegistrationUserResponseDto registerUser(RegistrationUserRequestDto request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя обязательно для заполнения");
        }
        if (request.getSurname() == null || request.getSurname().trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия обязательна для заполнения");
        }

        String login = generateUniqueLogin(request.getSurname().trim(), request.getName().trim());
        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setUsername(login);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setPasswordTemporary(true);
        user.setEnabled(true);
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPatronymic(request.getPatronymic());

        Role role = roleService.findByName(request.getRole());

        user.setRole(role);
        userRepository.save(user);

        log.info("new user with login: {}, password: {}", login, temporaryPassword);
        logAction("CREATE", user.getUsername(), "Создан пользователь с ролью " + user.getRole().getName());

        return new RegistrationUserResponseDto(login, temporaryPassword);
    }

    @Transactional
    public User changePassword(String username, String newPassword) {
        User user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordTemporary(false);
        logAction("PASSWORD_CHANGE", user.getUsername(), "Установлен постоянный пароль");
        return userRepository.save(user);
    }

    @Transactional
    public RegistrationUserResponseDto resetPassword(Long userId) {
        User user = findById(userId);

        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setPasswordTemporary(true);
        userRepository.save(user);

        log.info("Пароль сброшен для пользователя '{}'", user.getUsername());
        logAction("PASSWORD_RESET", user.getUsername(), "Сгенерирован новый временный пароль: " + temporaryPassword);

        return new RegistrationUserResponseDto(user.getUsername(), temporaryPassword);

    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User not found with username: " + username));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found with id: " + id));
    }

    public UserResponseDto getUserDtoByUsername(String username) {
        User user = findByUsername(username);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());

        return userResponseDto;
    }

    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void blockUser(Long userId) {
        User user = findById(userId);
        user.setEnabled(false);
        userRepository.save(user);
//        logAction("BLOCK", user.getUsername(), "Пользователь заблокирован");
    }

    public void unblockUser(Long userId) {
        User user = findById(userId);
        user.setEnabled(true);
        userRepository.save(user);
//        logAction("UNBLOCK", user.getUsername(), "Пользователь разблокирован");
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
//        logAction("DELETE", user.getUsername(), "Пользователь удалён");
    }

    public List<UserDashboardResponseDto> getAllUsers() {
        List<UserDashboardResponseDto> userDashboardResponseDtos = new ArrayList<>();
        List<User> all = userRepository.findAll();

        for (User user : all) {
            UserDashboardResponseDto userDashboardResponseDto = new UserDashboardResponseDto();
            userDashboardResponseDto.setId(user.getId());
            userDashboardResponseDto.setUsername(user.getUsername());
            userDashboardResponseDto.setFullName(user.getSurname() + " " + user.getName());
            userDashboardResponseDto.setRoleName(user.getRole().getName());
            userDashboardResponseDto.setEnabled(user.isEnabled());
            userDashboardResponseDto.setPasswordTemporary(user.isPasswordTemporary());

            userDashboardResponseDtos.add(userDashboardResponseDto);
        }

        return userDashboardResponseDtos;
    }

    public void changeRole(Long userId, String newRole) {
        Role byName = roleService.findByName(newRole);
        User user = findById(userId);
        user.setRole(byName);

        userRepository.save(user);
        logAction("ROLE_CHANGE", user.getUsername(), "Изменена роль на " + newRole);
    }

    private String generateUniqueLogin(String surname, String name) {
        String baseLogin = transliterateUtils.transliterateToLatin(surname) + "_" + transliterateUtils.transliterateToLatin(name.substring(0,1));
        String login = baseLogin;
        int counter = randomUtils.randomInt(1, 100);

        while (userRepository.existsByUsername(login)) {
            login = baseLogin + counter;
            counter++;
        }

        return login;
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(randomUtils.randomInt(chars.length())));
        }

        return password.toString();
    }

    private void logAction(String action, String targetUsername, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setTargetUsername(targetUsername);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}
