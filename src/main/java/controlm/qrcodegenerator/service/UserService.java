package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.RegistrationUserDto;
import controlm.qrcodegenerator.enums.RoleEnum;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Role;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(RegistrationUserDto registrationRequestDto) {
        User user = new User();
        user.setUsername(registrationRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));

        Role role = roleService.findByName(RoleEnum.ADMIN.getName());

        user.setRole(role);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User not found with username: " + username));
    }

    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
