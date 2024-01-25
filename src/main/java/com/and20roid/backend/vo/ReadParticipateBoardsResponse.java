package com.and20roid.backend.vo;

import com.and20roid.backend.entity.Board;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadParticipateBoardsResponse {

    private List<ReadParticipateBoardResponse> readBoardResponses;

    public ReadParticipateBoardsResponse(List<Board> boards) {
        this.readBoardResponses = boards.stream()
                .map(ReadParticipateBoardResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    static class ReadParticipateBoardResponse {
        private Long id;
        private String title;           // 제목
        private int participantNum;    // 참여자 수
        private int recruitmentNum;    // 모집자 수
        private String state;           // 모집 상태 -> 모집중 / 모집완료
        private String thumbnailUrl;   // 썸네일 URL
        private String createdDate;     // 생성일
        private String introLine;       // 한줄소개
        private boolean isDeleted;      // 삭제여부

        public ReadParticipateBoardResponse(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.participantNum = board.getParticipantNum();
            this.recruitmentNum = board.getRecruitmentNum();
            this.state = board.getState();
            this.thumbnailUrl = board.getThumbnailUrl();
            this.createdDate = board.getCreatedDate().toString();
            this.introLine = board.getIntroLine();
            this.isDeleted = board.isDeleted();
        }
    }
}
