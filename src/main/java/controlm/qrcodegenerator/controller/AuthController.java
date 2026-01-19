package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.RegistrationUserDto;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

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

    @PostMapping("/login")
    public String loginPost() {
        return "clients/list";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // Проверка на существующего пользователя
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            result.rejectValue("username", "error.user",
                    "Пользователь с таким именем уже существует");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername(user.getUsername());
        registrationUserDto.setPassword(user.getPassword());

        userService.create(registrationUserDto);

        log.info("Зарегистрирован новый пользователь: {}", user.getUsername());

        return "redirect:/login?registered=true";
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
    }
}
