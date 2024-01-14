package com.and20roid.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationRequest {

    private Long boardId;   // 참가할 게시물 id
    private String email;   // 참가자 이메일
}
