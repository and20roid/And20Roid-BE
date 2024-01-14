package com.and20roid.backend.vo;

import com.and20roid.backend.entity.Board;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadBoardsResponse {

    private List<ReadBoardResponse> readBoardResponses;

    public ReadBoardsResponse(List<Board> boards) {
        this.readBoardResponses = boards.stream()
                .map(ReadBoardResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    static class ReadBoardResponse {
        private Long id;
        private String title;           // 제목
        private int participantNum;    // 참여자 수
        private int recruitmentNum;    // 모집자 수
        private String state;           // 모집 상태 -> 모집중 / 모집완료
        private String thumbnailUrl;   // 썸네일 URL
        private String nickname;        // 작성자
        private String createdDate;     // 생성일
        private Long views;             // 조회수
        private Long likes;             // 좋아요수

        public ReadBoardResponse(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.participantNum = board.getParticipantNum();
            this.recruitmentNum = board.getRecruitmentNum();
            this.state = board.getState();
            this.thumbnailUrl = board.getThumbnailUrl();
            this.nickname = board.getUser().getNickname();
            this.createdDate = board.getCreatedDate().toString();
            this.views = board.getViews();
            this.likes = board.getLikes();
        }
    }

}
