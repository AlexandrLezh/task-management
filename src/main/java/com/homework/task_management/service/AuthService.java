package com.homework.task_management.service;

import com.homework.task_management.dto.RegisterRequest;
import com.homework.task_management.errors.UserAlreadyExistsException;
import com.homework.task_management.model.User;
import com.homework.task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.email(),
                encodedPassword
        );

        userRepository.save(user);
    }
}
