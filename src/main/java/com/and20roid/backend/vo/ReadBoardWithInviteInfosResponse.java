package com.and20roid.backend.vo;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadBoardWithInviteInfosResponse {

    private List<ReadBoardWithInviteInfoResponse> readBoardWithInviteInfoResponses;

    public ReadBoardWithInviteInfosResponse(List<ReadBoardWithInviteInfoQuery> queries) {
        this.readBoardWithInviteInfoResponses = queries.stream()
                .map(ReadBoardWithInviteInfoResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    static class ReadBoardWithInviteInfoResponse {
        private Long id;
        private String title;           // 제목
        private int participantNum;    // 참여자 수
        private int recruitmentNum;    // 모집자 수
        private String state;           // 모집 상태 -> 모집중 / 모집완료
        private String thumbnailUrl;   // 썸네일 URL
        private String introLine;       // 한 줄 소개
        private String createdDate;     // 생성일
        private boolean isAlreadyInvited;   // 좋아요 여부

        public ReadBoardWithInviteInfoResponse(ReadBoardWithInviteInfoQuery query) {
            this.id = query.getId();
            this.title = query.getTitle();
            this.participantNum = query.getParticipantNum();
            this.recruitmentNum = query.getRecruitmentNum();
            this.state = query.getState();
            this.thumbnailUrl = query.getThumbnailUrl();
            this.introLine = query.getIntroLine();
            this.createdDate = query.getCreatedDate().toString();
            this.isAlreadyInvited = (query.getIsAlreadyInvited() == 1);
        }
    }

}
