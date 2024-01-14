package com.and20roid.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadUserResponse {
    private int completedTestCount;
    private int uploadBoardCount;
}
