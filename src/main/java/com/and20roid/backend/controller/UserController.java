package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;

import com.and20roid.backend.service.FcmService;
import com.and20roid.backend.service.UserService;
import com.and20roid.backend.vo.CreateFcmTokenRequest;
import com.and20roid.backend.vo.ReadFcmMessagesResponse;
import com.and20roid.backend.vo.ReadUserTestingStats;
import com.and20roid.backend.vo.SignupRequest;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final FcmService fcmService;

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

    /**
     * FCM 토큰 생성
     */
    @PostMapping("/tokens")
    public ResponseEntity<String> createFcmToken(@RequestBody CreateFcmTokenRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        userService.createFcmToken(request.getToken(), userId);
        return new ResponseEntity<>("fcm 토큰 저장", HttpStatus.OK);
    }

    /**
     * FCM 토큰 삭제
     */
    @DeleteMapping("/tokens")
    public ResponseEntity<String> deleteFcmToken() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        userService.deleteFcmToken(userId);
        return new ResponseEntity<>("fcm 토큰 삭제", HttpStatus.OK);
    }

    /**
     * 알림함 조회
     */
    @GetMapping("/messages")
    public ResponseEntity<ReadFcmMessagesResponse> readFcmMessages(@Nullable @RequestParam Long lastMessageId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(fcmService.readMessages(userId, DEFAULT_ONE_PAGE_SIZE, lastMessageId), HttpStatus.OK);
    }
}
