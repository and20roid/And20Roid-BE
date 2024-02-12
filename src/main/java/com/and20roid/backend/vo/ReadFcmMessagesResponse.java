package com.and20roid.backend.vo;

import com.and20roid.backend.entity.FcmMessage;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadFcmMessagesResponse {

    private List<ReadFcmMessageResponse> fcmMessageResponses;

    public ReadFcmMessagesResponse(List<FcmMessage> fcmMessages) {
        this.fcmMessageResponses =  fcmMessages.stream()
                .map(ReadFcmMessageResponse::new)
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    static class ReadFcmMessageResponse {
        private long id;
        @Nullable
        private Long senderUserId;
        private String title;
        private String content;
        private String reqDate;
        private boolean successYn;
        private String type;
        private long boardId;
        private String thumbnailUrl;   // 썸네일 URL
        private String boardTitle;     // 제목
        private String introLine;       // 한 줄 소개
        private String appTestLink;     // 앱 테스트 링크
        private String webTestLink;     // 웹 테스트 링크

        public ReadFcmMessageResponse(FcmMessage fcmMessage) {
            this.id = fcmMessage.getId();
            this.senderUserId = fcmMessage.getSenderUserId();
            this.title = fcmMessage.getTitle();
            this.content = fcmMessage.getContent();
            this.reqDate = fcmMessage.getReqDate().toString();
            this.successYn = fcmMessage.isSuccessYn();
            this.type = fcmMessage.getType();
            if (fcmMessage.getBoard() != null) {
                this.boardId = fcmMessage.getBoard().getId();
                this.thumbnailUrl = fcmMessage.getBoard().getThumbnailUrl();
                this.boardTitle = fcmMessage.getBoard().getTitle();
                this.introLine = fcmMessage.getBoard().getIntroLine();
                this.appTestLink = fcmMessage.getBoard().getAppTestLink();
                this.webTestLink = fcmMessage.getBoard().getWebTestLink();
            }
        }
    }
}
