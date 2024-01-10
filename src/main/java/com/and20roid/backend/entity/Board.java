package com.and20roid.backend.entity;

import com.and20roid.backend.common.constant.Constant;
import com.and20roid.backend.vo.CreateBoardRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private String content;         // 내용
    private int participantNum;    // 참여자 수
    private int recruitmentNum;    // 모집자 수
    private String state;           // 모집 상태 -> 모집중 / 모집완료
    private String thumbnailUrl;   // 썸네일 URL
    private String appTestLink;     // 앱 테스트 링크
    private String webTestLink;     // 웹 테스트 링크

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
        this.appTestLink = createBoardRequest.getAppTestLink();
        this.webTestLink = createBoardRequest.getWebTestLink();
        this.user = user;
    }
}
