package com.and20roid.backend.service;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.FcmMessage;
import com.and20roid.backend.entity.FcmToken;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.FcmMessageRepository;
import com.and20roid.backend.repository.FcmTokenRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.CreateMessage;
import com.and20roid.backend.vo.ReadFcmMessagesResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    public static final String FCM_CLICK_ACTION = "clickAction";
    public static final String FCM_CLICK_ACTION_JOINTEST = "joinTest";

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmMessageRepository fcmMessageRepository;

    public void sendMessageByToken(CreateMessage createMessage) {
        log.info("start sendMessageByToken");

        User user = userRepository.findById(createMessage.getTargetUserId())
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        List<FcmToken> fcmTokens = fcmTokenRepository.findByUserId(user.getId());

        // 토큰 전송
        if (fcmTokens != null && !fcmTokens.isEmpty()) {
            Notification notification = Notification.builder()
                    .setTitle(createMessage.getTitle())
                    .setBody(createMessage.getBody())
                    .build();

            for (FcmToken fcmToken : fcmTokens) {
                Message message = Message.builder()
                        .setToken(fcmToken.getToken())
                        .setNotification(notification)
                        .putData(FCM_CLICK_ACTION, FCM_CLICK_ACTION_JOINTEST)
                        .build();
                try {
                    firebaseMessaging.send(message);
                    log.info("FCM 알림 전송 성공 Title: [{}], Body: [{}], targetUserId: [{}]", createMessage.getTitle(),
                            createMessage.getBody(), user.getId());
                    createMessage(createMessage, fcmToken.getToken(), LocalDateTime.now(), true);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    log.info("FCM 알림 전송 실패 targetUserId: [{}]", user.getId());
                }
            }
        } else {
            log.info("서버에 userId: [{}]인 유저의 FCM 토큰 정보가 존재하지 않아, 알림을 전송할 수 없습니다.", user.getId());
            createMessage(createMessage, null, LocalDateTime.now(), false);
        }
    }

    private void createMessage(CreateMessage createMessage, String token, LocalDateTime reqDate, boolean successYn) {
        log.info("start createMessage by userId: [{}], type: [{}], token: [{}], successYn: [{}]", createMessage.getTargetUserId(), createMessage.getType(), token, successYn);

        fcmMessageRepository.save(
                new FcmMessage(createMessage.getTargetUserId(), token, reqDate, createMessage.getTitle(),
                        createMessage.getBody(), createMessage.getType(), successYn, createMessage.getBoard()));
    }

    public ReadFcmMessagesResponse readMessages(long userId, int pageSize) {
        log.info("start readMessages by userId: [{}]", userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, pageSize, sort);

        Page<FcmMessage> fcmMessagePage = fcmMessageRepository.findAll(pageRequest);

        return new ReadFcmMessagesResponse(fcmMessagePage.getContent());
    }
}
