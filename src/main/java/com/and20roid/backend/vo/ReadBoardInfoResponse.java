package com.and20roid.backend.vo;

import com.and20roid.backend.entity.Board;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadBoardInfoResponse {

    private String content;             // 내용
    private List<String> imageUrls;     // 이미지 링크
    private int participantNum;         // 참여자 수
    private String appTestLink;     // 앱 테스트 링크
    private String webTestLink;     // 웹 테스트 링크
    private Long views;             // 조회수
    private Long likes;             // 좋아요 수

    public ReadBoardInfoResponse(Board board, List<String> imageUrls) {
        this.content = board.getContent();
        this.imageUrls = imageUrls;
        this.participantNum = board.getParticipantNum();
        this.appTestLink = board.getAppTestLink();
        this.webTestLink = board.getWebTestLink();
        this.views = board.getViews();
        this.likes = board.getLikes();
    }
}
