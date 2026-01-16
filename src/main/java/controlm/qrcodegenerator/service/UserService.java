package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.RegistrationUserDto;
import controlm.qrcodegenerator.enums.RoleEnum;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Role;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService  {
    private final RoleService roleService;
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(RegistrationUserDto registrationRequestDto) {
        User user = new User();
        user.setUsername(registrationRequestDto.getUsername());
//        user.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setPassword(registrationRequestDto.getPassword());

        Role role = roleService.findByName(RoleEnum.ADMIN.getName());

        user.setRole(role);

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> userOptional = userRepository.findByUsername(username);
//        User user = userOptional.orElseThrow(() -> new NotFoundException("User with username = " + username + " not found"));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
//        );
//    }
}
