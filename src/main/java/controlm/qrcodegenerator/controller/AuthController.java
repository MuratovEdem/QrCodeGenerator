package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.RegistrationUserRequestDto;
import controlm.qrcodegenerator.dto.response.RegistrationUserResponseDto;
import controlm.qrcodegenerator.dto.response.UserResponseDto;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.service.RoleService;
import controlm.qrcodegenerator.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String registerPage(Model model) {
        List<String> roles = roleService.findAll();
        model.addAttribute("roles", roles);

        return "auth/register";
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<RegistrationUserResponseDto> registerUser(@RequestBody RegistrationUserRequestDto user) {
        try {
            RegistrationUserResponseDto response = userService.registerUser(user);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации при создании пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, Authentication authentication) {
        UserResponseDto userDtoByUsername = userService.getUserDtoByUsername(authentication.getName());
        model.addAttribute("user", userDtoByUsername);

        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordPost(@RequestParam String newPassword,
                                     @RequestParam String confirmPassword,
                                     Authentication authentication,
                                     Model model,
                                     HttpServletRequest request) {
        if (!newPassword.equals(confirmPassword)) {
            UserResponseDto userDtoByUsername = userService.getUserDtoByUsername(authentication.getName());
            model.addAttribute("user", userDtoByUsername);
            model.addAttribute("errorMessage", "Пароли не совпадают");
            return "auth/change-password";
        }

        try {
            User updatedUser = userService.changePassword(authentication.getName(), newPassword);
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    updatedUser,
                    authentication.getCredentials(),
                    updatedUser.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );
            }
            model.addAttribute("successMessage", "Пароль успешно изменен");
            return "redirect:/clients";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при смене пароля");
            return "auth/change-password";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal User currentUser,
                              Model model) {
        model.addAttribute("user", currentUser);
        return "auth/profile";
        // TODO
    }
}
