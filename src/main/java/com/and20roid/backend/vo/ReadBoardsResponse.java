package com.and20roid.backend.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadBoardsResponse {

    private List<ReadBoardResponse> readBoardResponses;

    public ReadBoardsResponse(List<ReadBoardQuery> readBoardQueries) {
        this.readBoardResponses = readBoardQueries.stream()
                .map(ReadBoardResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    public static class ReadBoardResponse {
        private Long id;
        private String title;           // 제목
        private int participantNum;    // 참여자 수
        private int recruitmentNum;    // 모집자 수
        private String state;           // 모집 상태 -> 모집중 / 모집완료
        private String thumbnailUrl;   // 썸네일 URL
        private List<String> imageUrls; // 앱 소개 이미지 url 리스트
        private String introLine;       // 한 줄 소개
        private String nickname;        // 작성자
        private String createdDate;     // 생성일
        private Long views;             // 조회수
        private Long likes;             // 좋아요수
        private boolean isLikedBoard;   // 좋아요 여부

        public ReadBoardResponse(ReadBoardQuery readBoardQuery) {
            String imageUrlConcat = readBoardQuery.getImageUrls();
            List<String> imageUrls = new ArrayList<>();

            if (imageUrls != null) {
                imageUrls = Arrays.stream(imageUrlConcat.split(",")).toList();
            }

            this.id = readBoardQuery.getId();
            this.title = readBoardQuery.getTitle();
            this.participantNum = readBoardQuery.getParticipantNum();
            this.recruitmentNum = readBoardQuery.getRecruitmentNum();
            this.state = readBoardQuery.getState();
            this.thumbnailUrl = readBoardQuery.getThumbnailUrl();
            this.imageUrls = imageUrls;
            this.introLine = readBoardQuery.getIntroLine();
            this.nickname = readBoardQuery.getNickname();
            this.createdDate = readBoardQuery.getCreatedDate().toString();
            this.views = readBoardQuery.getViews();
            this.likes = readBoardQuery.getLikes();
            this.isLikedBoard = (readBoardQuery.getIsLikedBoard() == 1);
        }
    }
}
