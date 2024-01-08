package com.and20roid.backend.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;

@Getter
@JsonFormat(shape = Shape.OBJECT)
public enum CommonCode {
    // SUCCESS
    SUCCESS(200, 200, "성공"),

    // FAIL
    FAIL(500, -1, "실패. 알 수 없는 오류"),
    INVALID_ELEMENTS(400, -2, "조건에 맞지 않는 요소(elements)가 있습니다"),

    //-1000: User
    NONEXISTENT_USER(400, -1000, "존재하지 않는 유저입니다."),

    //-2000: Board
    NONEXISTENT_BOARD(400, -2000, "존재하지 않는 게시글입니다."),
    ZERO_INTRODUCTION_IMAGES(400, -2001, "소개 이미지가 존재하지 않습니다."),

    //-3000: MultipartFile
    WRONG_FILE_FORMAT(400, -3000, "잘못된 형식의 파일입니다."),
    FILE_UPLOAD_FAIL(400, -3001, "파일 업로드에 실패했습니다")
    ;


    private int status;
    private int code;
    private String message;

    CommonCode(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
