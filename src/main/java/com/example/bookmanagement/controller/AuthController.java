package com.example.bookmanagement.controller;

import com.example.bookmanagement.dto.RegisterRequest;
import com.example.bookmanagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute RegisterRequest request, Model model) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "register";
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "用户名不能为空");
            return "register";
        }
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            model.addAttribute("error", "密码长度至少为 4 位");
            return "register";
        }
        boolean success = userService.register(request.getUsername().trim(), request.getPassword());
        if (!success) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
        return "redirect:/login?registered=true";
    }
}
