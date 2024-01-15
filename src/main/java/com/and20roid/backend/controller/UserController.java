package com.and20roid.backend.controller;

import com.and20roid.backend.service.UserService;
import com.and20roid.backend.vo.ReadUserTestingStats;
import com.and20roid.backend.vo.SignupRequest;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody SignupRequest request) {
        return new ResponseEntity(userService.signup(request.getUid(), request.getNickname()), HttpStatus.OK);
    }

    /**
     * 유저의 테스트 참여 / 의뢰 횟수 조회
     * userId가 null인 경우 -> 자신의 정보 조회
     * userId가 null이 아닌 경우 -> 입력한 유저의 정보 조회
     */
    @GetMapping("")
    public ResponseEntity<ReadUserTestingStats> readUserTestingStats(@Nullable @RequestParam Long userId) {

        if (userId == null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userId = Long.parseLong(userDetails.getUsername());
        }

        return new ResponseEntity<>(userService.readUserTestingStats(userId), HttpStatus.OK);
    }
}
