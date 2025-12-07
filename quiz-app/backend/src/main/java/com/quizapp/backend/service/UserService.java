package com.quizapp.backend.service;

import com.quizapp.backend.model.User;
import com.quizapp.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return Objects.requireNonNull(userRepository.save(user));
    }

    public User login(String username, String password) {
        java.util.List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return null;
        }

        // Self-heal duplicates: keep the first one, delete others
        if (users.size() > 1) {
            for (int i = 1; i < users.size(); i++) {
                userRepository.delete(users.get(i));
            }
        }

        User user = users.get(0);
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
