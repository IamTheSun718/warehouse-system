package com.demo.controller;

import com.demo.model.User;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.findByUsername(username);
        Map<String, Object> result = new HashMap<>();

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        String token = UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("realName", user.getRealName());

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("user", userInfo);

        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            result.put("success", false);
            result.put("message", "密码不能为空");
            return result;
        }

        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        User newUser = userService.register(user);
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("user", newUser);

        return result;
    }
}