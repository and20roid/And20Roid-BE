package com.and20roid.backend.entity;

import com.and20roid.backend.common.constant.Constant;
import com.and20roid.backend.vo.CreateBoardRequest;
import com.and20roid.backend.vo.UpdateBoardRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;           // 제목

    @Column(length = 500)
    private String content;         // 내용

    private int participantNum;    // 참여자 수
    private int recruitmentNum;    // 모집자 수
    private String state;           // 모집 상태 -> 모집중 / 모집완료
    private String thumbnailUrl;   // 썸네일 URL

    @Column(length = 100)
    private String introLine;       // 한 줄 소개

    private String appTestLink;     // 앱 테스트 링크
    private String webTestLink;     // 웹 테스트 링크
    private Long views;             // 조회수
    private Long likes;             // 좋아요 개수

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;      // 삭제 여부

    @Nullable
    private LocalDateTime startTime;    // 테스트 시작 시간

    @Nullable
    private Boolean fcmSentByScheduler; // 스케줄러에 의해 fcm 메시지가 전송되었는지 여부

    @Nullable
    private LocalDateTime endTime;    // 테스트 종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Board(CreateBoardRequest createBoardRequest, String thumbnailUrl, User user) {
        this.title = createBoardRequest.getTitle();
        this.content = createBoardRequest.getContent();
        this.participantNum = 0;
        this.recruitmentNum = createBoardRequest.getRecruitmentNum();
        this.state = Constant.BOARD_STATE_OPEN;
        this.thumbnailUrl = thumbnailUrl;
        this.introLine = createBoardRequest.getIntroLine();
        this.appTestLink = createBoardRequest.getAppTestLink();
        this.webTestLink = createBoardRequest.getWebTestLink();
        this.user = user;
        this.views = 0L;
        this.likes = 0L;
        this.startTime = null;
        this.fcmSentByScheduler = false;
        this.endTime = null;
        this.isDeleted = false;
    }

    public Board addViews() {
        this.views += 1;
        return this;
    }

    public Board addParticipantNum() {
        this.participantNum += 1;
        return this;
    }

    public Board subtractParticipantNum() {
        this.participantNum -= 1;
        return this;
    }

    public Board addLikes() {
        this.likes += 1;
        return this;
    }

    public Board cancelLikes() {
        this.likes -= 1;
        return this;
    }

    public Board updateStatus(String state) {
        this.state = state;
        return this;
    }

    public Board updateStartTime() {
        this.startTime = LocalDateTime.now();
        return this;
    }

    public Board updateFcmSentByScheduler(boolean fcmSentByScheduler) {
        this.fcmSentByScheduler = fcmSentByScheduler;
        return this;
    }

    public Board updateEndTime() {
        this.endTime = LocalDateTime.now();
        return this;
    }

    public Board withdrawalUser() {
        this.user = null;
        this.isDeleted = true;
        return this;
    }

    public Board updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    public void update(UpdateBoardRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.recruitmentNum = request.getRecruitmentNum();
        this.introLine = request.getIntroLine();
        this.appTestLink = request.getAppTestLink();
        this.webTestLink = request.getWebTestLink();
    }

    public Board delete() {
        this.isDeleted = true;
        return this;
    }
}
