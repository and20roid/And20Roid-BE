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
    ALREADY_EXIST_USER(400, -1001, "이미 존재하는 유저입니다."),
    ALREADY_EXIST_NICKNAME(400, -1002, "이미 존재하는 닉네임입니다."),

    //-2000: Board
    NONEXISTENT_BOARD(400, -2000, "존재하지 않는 게시글입니다."),
    ZERO_INTRODUCTION_IMAGES(400, -2001, "소개 이미지가 존재하지 않습니다."),
    TOO_MANY_IMAGES(400, -2002, "이미지는 최대 3개까지만 첨부할 수 있습니다."),
    FORBIDDEN_BOARD(403, -2003, "접근할 수 없는 게시글입니다."),
    INVALID_BOARD_STATE(400, -2004, "이미 진행 중이거나, 종료된 테스트입니다."),

    //-3000: MultipartFile
    WRONG_FILE_FORMAT(400, -3000, "잘못된 형식의 파일입니다."),
    FILE_UPLOAD_FAIL(400, -3001, "파일 업로드에 실패했습니다"),

    //-4000: participation
    ALREADY_PARTICIPATE_BOARD(400, -4000, "이미 참여한 테스트입니다."),
    CANNOT_PARTICIPATE_OWN_BOARD(400, -4001, "자신의 테스트는 참여하실 수 없습니다."),
    INVALID_BOARD_STATE_FOR_INVITE(400, -4002, "모집 중이 아닌 모집 글에 참여를 요청하실 수 없습니다."),
    ALREADY_PARTICIPATE_USER(400, -4003, "이미 테스트에 참여한 유저입니다."),
    ALREADY_INVITED_USER(400, -4004, "이미 테스트에 초대된 유저입니다.");


    private int status;
    private int code;
    private String message;

    CommonCode(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
