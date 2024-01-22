package com.and20roid.backend.vo;

import com.and20roid.backend.entity.Board;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String nickname;        // 작성자 닉네임
    private Long views;             // 조회수
    private Long likes;             // 좋아요 수
    private boolean isLikedBoard;   // 좋아요 여부

    public ReadBoardInfoResponse(Board board, ReadBoardQuery readBoardQuery) {
        String imageUrlConcat = readBoardQuery.getImageUrls();
        List<String> imageUrls = new ArrayList<>();
        imageUrls = Arrays.stream(imageUrlConcat.split(",")).toList();

        this.content = board.getContent();
        this.imageUrls = imageUrls;
        this.participantNum = board.getParticipantNum();
        this.appTestLink = board.getAppTestLink();
        this.webTestLink = board.getWebTestLink();
        this.nickname = readBoardQuery.getNickname();
        this.views = board.getViews();
        this.likes = board.getLikes();
        this.isLikedBoard = (readBoardQuery.getIsLikedBoard() == 1);
    }
}
