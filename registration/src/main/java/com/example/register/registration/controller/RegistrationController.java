package com.example.register.registration.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.register.registration.model.Users;
import com.example.register.registration.repository.UserJpaRepository;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {

    @Autowired
    private UserJpaRepository userService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/")
    public String showHomePage() {

        return "home"; // Return the view name for home page
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register"; // returns the view register
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Users user, Model model) {
        if (userService.existsByEmailId(user.getEmailId())) {
            model.addAttribute("error", "User with this email already exists!");
            return "register"; // Return back to the registration form with error message
        }

        userService.save(user);
        return "redirect:/register?success"; // Redirect to the register page with a success parameter
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new Users());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") Users user, Model model) {
        Users existingUser = userService.findByEmailId(user.getEmailId());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            return "redirect:/login?success"; // Redirect to the login page after successful login
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {

        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(@RequestParam("email") String email, HttpServletRequest request) {
        Users user = userService.findByEmailId(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userService.save(user);
            String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/reset_password?token=" + token;
            sendResetPasswordEmail(email, resetLink);
            return "forgot_password_success";
        } else {
            return "forgot_password_error";
        }
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Users user = userService.findByResetToken(token);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("token", token);
            return "reset_password";
        } else {
            return "reset_password_error";
        }
    }

    @PostMapping("/reset_password")
    public String processResetPasswordForm(@RequestParam("token") String token,
                                           @RequestParam("password") String password,
                                           @RequestParam("confirmPassword") String confirmPassword) {
        Users user = userService.findByResetToken(token);
        if (user != null) {
            if (!password.equals(confirmPassword)) {
                return "reset_password_error";
            }

            user.setPassword(password);
            user.setResetToken(null);
            userService.save(user);
            return "reset_password_success";
        } else {
            return "reset_password_error";
        }
    }


    private void sendResetPasswordEmail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Your Password");
        message.setText("To reset your password, please click the link below:\n\n" + resetLink);
        mailSender.send(message);
    }
}
