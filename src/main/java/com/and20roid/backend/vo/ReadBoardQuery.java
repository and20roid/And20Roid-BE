package com.and20roid.backend.vo;

import java.time.LocalDateTime;

public interface ReadBoardQuery {
    Long getId();
    String getTitle();
    int getParticipantNum();
    int getRecruitmentNum();
    String getState();
    String getIntroLine();
    String getThumbnailUrl();
    String getImageUrls();
    String getNickname();
    LocalDateTime getCreatedDate();
    Long getViews();
    Long getLikes();
    int getIsLikedBoard();
    int getIsMine();
}
