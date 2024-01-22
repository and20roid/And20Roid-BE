package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;

import com.and20roid.backend.service.BoardService;
import com.and20roid.backend.vo.CreateBoardRequest;
import com.and20roid.backend.vo.ReadBoardInfoResponse;
import com.and20roid.backend.vo.ReadBoardsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * 모집글 생성
     */
    @PostMapping("")
    public ResponseEntity createBoard(@RequestPart(value = "dto") CreateBoardRequest createBoardRequest,
                                      @RequestPart(value = "thumbnail") MultipartFile thumbnailFile,
                                      @RequestPart(value = "images") List<MultipartFile> multipartFiles)
            throws FileUploadException {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        boardService.createBoard(createBoardRequest, thumbnailFile, multipartFiles, userId);

        return ResponseEntity.ok("success");
    }

    /**
     * 모집글 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ReadBoardsResponse> readBoards(@RequestParam Long lastBoardId) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(boardService.readBoards(lastBoardId, DEFAULT_ONE_PAGE_SIZE, userId), HttpStatus.OK);
    }

    /**
     * 모집글 상세 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ReadBoardInfoResponse> readBoard(@PathVariable Long boardId) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(boardService.readBoard(boardId, userId), HttpStatus.OK);
    }

    /**
     * 모집글 좋아요
     */
    @PostMapping("/{boardId}/likes")
    public ResponseEntity updateBoardLikes(@PathVariable Long boardId) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        String msg = boardService.updateBoardLikes(boardId, userId);

        return ResponseEntity.ok(msg);
    }
}
