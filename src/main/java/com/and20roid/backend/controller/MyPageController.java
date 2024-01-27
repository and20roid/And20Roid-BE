package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;

import com.and20roid.backend.service.MyPageService;
import com.and20roid.backend.vo.ReadParticipateBoardsResponse;
import com.and20roid.backend.vo.ReadUploadBoardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myPages")
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 업로드한 모집글 조회
     */
    @GetMapping("/boards/upload")
    public ResponseEntity<ReadUploadBoardsResponse> readUploadBoards(@RequestParam Long lastBoardId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(myPageService.readUploadBoards(lastBoardId, DEFAULT_ONE_PAGE_SIZE, userId),
                HttpStatus.OK);
    }

    /**
     * 참여한 모집글 조회
     */
    @GetMapping("/boards/participation")
    public ResponseEntity<ReadParticipateBoardsResponse> readParticipateBoards(@RequestParam Long lastBoardId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(myPageService.readParticipateBoards(lastBoardId, DEFAULT_ONE_PAGE_SIZE, userId),
                HttpStatus.OK);
    }
}
