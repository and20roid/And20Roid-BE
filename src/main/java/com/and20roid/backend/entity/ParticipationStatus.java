package com.and20roid.backend.entity;

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
public class ParticipationStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String status;      // 상태 / "대기중", "테스트 진행 중", "테스트 완료"
    private String email;       // 이메일

    public ParticipationStatus(User user, Board board, String status, String email) {
        this.user = user;
        this.board = board;
        this.status = status;
        this.email = email;
    }

    public ParticipationStatus updateStatus(String status) {
        this.status = status;
        return this;
    }
}
