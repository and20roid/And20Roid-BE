package com.and20roid.backend.controller;

import com.and20roid.backend.service.UserService;
import com.and20roid.backend.vo.ReadUserResponse;
import com.and20roid.backend.vo.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody SignupRequest request) {
        return new ResponseEntity(userService.signup(request.getUid(), request.getNickname()), HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<ReadUserResponse> readUser(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.readUser(userId), HttpStatus.OK);
    }
}
