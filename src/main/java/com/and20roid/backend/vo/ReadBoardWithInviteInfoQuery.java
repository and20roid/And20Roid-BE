package com.and20roid.backend.vo;

import java.time.LocalDateTime;

public interface ReadBoardWithInviteInfoQuery {
    Long getId();
    String getTitle();
    int getParticipantNum();
    int getRecruitmentNum();
    String getState();
    String getIntroLine();
    String getThumbnailUrl();
    LocalDateTime getCreatedDate();

    int getIsAlreadyInvited();
}
