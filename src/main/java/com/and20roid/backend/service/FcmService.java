package com.and20roid.backend.service;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.FcmMessage;
import com.and20roid.backend.entity.FcmToken;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.FcmMessageRepository;
import com.and20roid.backend.repository.FcmTokenRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.CreateMessageRequest;
import com.and20roid.backend.vo.ReadFcmMessagesResponse;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    public static final String FCM_CLICK_ACTION = "clickAction";

    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmMessageRepository fcmMessageRepository;

    public void sendMessage(CreateMessageRequest request) {
        log.info("start sendMessage");

        User user = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        List<FcmToken> fcmTokens = fcmTokenRepository.findByUserId(user.getId());

        // 토큰 전송
        if (fcmTokens != null && !fcmTokens.isEmpty()) {
            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            try {
                sendMessageByTokens(notification, fcmTokens, request.getType());
                log.info("FCM 알림 전송 성공 Title: [{}], Body: [{}], targetUserId: [{}]", request.getTitle(),
                        request.getBody(), user.getId());
                createMessage(request, fcmTokens.get(0).getToken(), LocalDateTime.now(), true);
            } catch (CustomException e) {
                createMessage(request, null, LocalDateTime.now(), false);
                log.info("FCM 알림 전송 실패 targetUserId: [{}]", user.getId());
            }
        }
        else {
            log.info("서버에 userId: [{}]인 유저의 FCM 토큰 정보가 존재하지 않아, 알림을 전송할 수 없습니다.", user.getId());
            createMessage(request, null, LocalDateTime.now(), false);
        }
    }

    @Async
    public void sendMessageByTokens(Notification notification, List<FcmToken> fcmTokens, String clickAction) {
        log.info("start sendMessageByTokens");

        List<Message> messages = fcmTokens.stream()
                .map(fcmToken -> createMessageByBuilder(fcmToken.getToken(), notification, clickAction))
                .toList();

        try {
            BatchResponse batchResponse = sendAndGetResponses(messages);
            log.info("Sent tokens: [{}]", batchResponse);
        } catch (Exception e) {
            log.error("error in sendMessageByTokens : {}", e.getMessage());
            throw new CustomException(e.getMessage(), CommonCode.FAIL_SEND_FCM);
        }
    }

    private void createMessage(CreateMessageRequest createMessageRequest, String token, LocalDateTime reqDate, boolean successYn) {
        log.info("start createMessage by userId: [{}], type: [{}], token: [{}], successYn: [{}]", createMessageRequest.getTargetUserId(), createMessageRequest.getType(), token, successYn);

        fcmMessageRepository.save(
                new FcmMessage(createMessageRequest.getTargetUserId(), createMessageRequest.getSenderUserId(), token, reqDate, createMessageRequest.getTitle(),
                        createMessageRequest.getBody(), createMessageRequest.getType(), successYn, createMessageRequest.getBoard()));
    }

    public ReadFcmMessagesResponse readMessages(long userId, int pageSize, Long lastMessageId) {
        log.info("start readMessages by userId: [{}]", userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, pageSize, sort);

        Page<FcmMessage> fcmMessagePage = null;

        if (lastMessageId == null || lastMessageId < 1) {
            fcmMessagePage = fcmMessageRepository.findByUserId(userId, pageRequest);
        } else {
            fcmMessagePage = fcmMessageRepository.findByIdLessThanAndUserId(lastMessageId, userId, pageRequest);
        }

        return new ReadFcmMessagesResponse(fcmMessagePage.getContent());
    }

    private BatchResponse sendAndGetResponses(List<Message> messages) throws ExecutionException, InterruptedException {
        return FirebaseMessaging.getInstance().sendAllAsync(messages).get();
    }

    private Message createMessageByBuilder(String fcmToken, Notification notification, String clickAction) {
        return Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData(FCM_CLICK_ACTION, clickAction)
                .build();
    }
}
