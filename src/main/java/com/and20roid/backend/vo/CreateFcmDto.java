package com.and20roid.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFcmDto {

    private long targetUserId;
    private String title;
    private String body;

}
