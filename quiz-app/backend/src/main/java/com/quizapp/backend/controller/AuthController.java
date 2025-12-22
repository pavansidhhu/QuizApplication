package com.quizapp.backend.controller;

import com.quizapp.backend.model.User;
import com.quizapp.backend.service.UserService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.register(user));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "error in register";
            return ResponseEntity.ok(errorMessage);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User loggedInUser = userService.login(user.getUsername(), user.getPassword());

            if (loggedInUser != null) {
                return ResponseEntity.ok(
                        Map.of(
                                "userId", loggedInUser.getId(),
                                "role", loggedInUser.getRole().toUpperCase()));
            }

            return ResponseEntity.status(401).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error in login");
        }
    }

}
