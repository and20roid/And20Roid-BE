package com.and20roid.backend.service;

import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String signup(String token, String nickname) {
        userRepository.save(new User(token, nickname));

        return "성공";
    }
}
