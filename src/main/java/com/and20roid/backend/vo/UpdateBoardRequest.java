package com.and20roid.backend.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBoardRequest {

    private String title;           // 제목
    private String content;         // 내용
    private int recruitmentNum;    // 모집자 수
    private String introLine;       // 한 줄 소개
    private String appTestLink;     // 앱 테스트 링크
    private String webTestLink;     // 웹 테스트 링크
    private List<String> deleteImageUrls; // 삭제할 이미지 urls
}
