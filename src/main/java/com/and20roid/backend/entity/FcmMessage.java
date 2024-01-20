package com.and20roid.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class FcmMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId; // targetUserId
    private String token; // fcm
    private LocalDateTime reqDate; // 전송일시
    private String title;
    private String content;
    private String type; // 메시지 타입 -> request, join, start, end
    private boolean successYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public FcmMessage(long userId, String token, LocalDateTime reqDate, String title, String content, String type,
                      boolean successYn, Board board) {
        this.userId = userId;
        this.token = token;
        this.reqDate = reqDate;
        this.title = title;
        this.content = content;
        this.type = type;
        this.successYn = successYn;
        this.board = board;
    }
}
