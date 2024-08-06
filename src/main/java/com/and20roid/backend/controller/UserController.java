package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;
import static com.and20roid.backend.service.UserService.USER_INFO_REQUEST_TYPE_ANOTHER;
import static com.and20roid.backend.service.UserService.USER_INFO_REQUEST_TYPE_MY;

import com.and20roid.backend.common.auth.AuthUser;
import com.and20roid.backend.service.FcmService;
import com.and20roid.backend.service.UserService;
import com.and20roid.backend.vo.AuthUserDTO;
import com.and20roid.backend.vo.CreateFcmTokenRequest;
import com.and20roid.backend.vo.ReadFcmMessagesResponse;
import com.and20roid.backend.vo.ReadUserTestingStats;
import com.and20roid.backend.vo.SignupRequest;
import com.and20roid.backend.vo.UpdateUserRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<ReadUserTestingStats> readUserTestingStats(@AuthUser AuthUserDTO authUserDTO,
                                                                     @Nullable @RequestParam Long userId) {
        Long myUserId = authUserDTO.getUserId();

        if (userId == null) {
            return new ResponseEntity<>(userService.readUserTestingStats(myUserId, myUserId, USER_INFO_REQUEST_TYPE_MY),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(userService.readUserTestingStats(userId, myUserId, USER_INFO_REQUEST_TYPE_ANOTHER),
                HttpStatus.OK);
    }

    /**
     * FCM 토큰 생성
     */
    @PostMapping("/tokens")
    public ResponseEntity<String> createFcmToken(@AuthUser AuthUserDTO authUserDTO,
                                                 @RequestBody CreateFcmTokenRequest request) {
        userService.createFcmToken(request.getToken(), authUserDTO.getUserId());
        return new ResponseEntity<>("fcm 토큰 저장", HttpStatus.OK);
    }

    /**
     * FCM 토큰 삭제
     */
    @DeleteMapping("/tokens")
    public ResponseEntity<String> deleteFcmToken(@AuthUser AuthUserDTO authUserDTO) {
        userService.deleteFcmToken(authUserDTO.getUserId());
        return new ResponseEntity<>("fcm 토큰 삭제", HttpStatus.OK);
    }

    /**
     * 알림함 조회
     */
    @GetMapping("/messages")
    public ResponseEntity<ReadFcmMessagesResponse> readFcmMessages(@AuthUser AuthUserDTO authUserDTO,
                                                                   @Nullable @RequestParam Long lastMessageId) {
        return new ResponseEntity<>(
                fcmService.readMessages(authUserDTO.getUserId(), DEFAULT_ONE_PAGE_SIZE, lastMessageId), HttpStatus.OK);
    }

    /**
     * 닉네임 변경
     */
    @PutMapping("")
    public ResponseEntity<String> updateUser(@AuthUser AuthUserDTO authUserDTO,
                                             @RequestBody UpdateUserRequest request) {
        userService.updateUser(request.getNickname(), authUserDTO.getUserId());
        return new ResponseEntity<>("유저 업데이트 성공", HttpStatus.OK);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("")
    public ResponseEntity<String> deleteUser(@AuthUser AuthUserDTO authUserDTO) {
        userService.deleteUser(authUserDTO.getUserId());
        return new ResponseEntity<>("회원 탈퇴 성공", HttpStatus.OK);
    }
}
