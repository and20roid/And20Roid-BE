package com.and20roid.backend.service;

import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public String signup(String token, String nickname) {
        log.info("signup by token[{}], nickname[{}]", token, nickname);

        User savedUser = userRepository.save(new User(token, nickname));
        authorityRepository.save(new Authority(savedUser, ROLE_USER));

        return "성공";
    }
}
