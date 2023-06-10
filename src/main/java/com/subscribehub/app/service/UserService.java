package com.subscribehub.app.service;

import com.subscribehub.app.domain.User;
import com.subscribehub.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User findOneByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }
}
