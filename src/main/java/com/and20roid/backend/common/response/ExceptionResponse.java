package com.and20roid.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {

    private Boolean isSuccess;
    private int status;
    private int code; // common code
    private String message;

    public ExceptionResponse(CommonCode commonCode, String message) {
        this.isSuccess = false;
        this.status = commonCode.getStatus();
        this.code = commonCode.getCode();
        this.message = message;
    }
}
