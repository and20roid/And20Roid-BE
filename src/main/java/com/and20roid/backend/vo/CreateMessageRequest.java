package com.and20roid.backend.vo;

import com.and20roid.backend.entity.Board;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {

    private long targetUserId;

    @Nullable
    private Long senderUserId;

    private String title;
    private String body;
    private String type;

    @Nullable
    private Board board;
}
