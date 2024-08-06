package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;

import com.and20roid.backend.common.auth.AuthUser;
import com.and20roid.backend.service.BoardService;
import com.and20roid.backend.vo.AuthUserDTO;
import com.and20roid.backend.vo.CreateBoardRequest;
import com.and20roid.backend.vo.ReadBoardInfoResponse;
import com.and20roid.backend.vo.ReadBoardsResponse;
import com.and20roid.backend.vo.UpdateBoardRequest;
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity createBoard(@AuthUser AuthUserDTO authUserDTO,
                                      @RequestPart(value = "dto") CreateBoardRequest createBoardRequest,
                                      @RequestPart(value = "thumbnail") MultipartFile thumbnailFile,
                                      @RequestPart(value = "images") List<MultipartFile> multipartFiles)
            throws FileUploadException {
        boardService.createBoard(createBoardRequest, thumbnailFile, multipartFiles, authUserDTO.getUserId());
        return ResponseEntity.ok("success");
    }

    /**
     * 모집글 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<ReadBoardsResponse> readBoards(@AuthUser AuthUserDTO authUserDTO,
                                                         @RequestParam Long lastBoardId) {
        return new ResponseEntity<>(
                boardService.readBoards(lastBoardId, DEFAULT_ONE_PAGE_SIZE, authUserDTO.getUserId()), HttpStatus.OK);
    }

    /**
     * 모집글 수정
     */
    @PutMapping("/{boardId}")
    public ResponseEntity updateBoard(@AuthUser AuthUserDTO authUserDTO,
                                      @PathVariable Long boardId,
                                      @RequestPart(value = "dto") UpdateBoardRequest request,
                                      @Nullable @RequestPart(value = "thumbnail") MultipartFile thumbnailFile,
                                      @Nullable @RequestPart(value = "images") List<MultipartFile> multipartFiles)
            throws FileUploadException {
        boardService.updateBoard(boardId, authUserDTO.getUserId(), request, thumbnailFile, multipartFiles);
        return ResponseEntity.ok("success");
    }

    /**
     * 모집글 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity deleteBoard(@AuthUser AuthUserDTO authUserDTO,
                                      @PathVariable Long boardId) {
        boardService.deleteBoard(boardId, authUserDTO.getUserId());
        return ResponseEntity.ok("success");
    }

    /**
     * 모집글 상세 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ReadBoardInfoResponse> readBoard(@AuthUser AuthUserDTO authUserDTO,
                                                           @PathVariable Long boardId) {
        return new ResponseEntity<>(boardService.readBoard(boardId, authUserDTO.getUserId()), HttpStatus.OK);
    }

    /**
     * 모집글 좋아요
     */
    @PostMapping("/{boardId}/likes")
    public ResponseEntity updateBoardLikes(@AuthUser AuthUserDTO authUserDTO,
                                           @PathVariable Long boardId) {
        String msg = boardService.updateBoardLikes(boardId, authUserDTO.getUserId());
        return ResponseEntity.ok(msg);
    }

    /**
     * 테스트 시작
     */
    @PostMapping("/{boardId}/start")
    public ResponseEntity<String> startTest(@AuthUser AuthUserDTO authUserDTO,
                                            @PathVariable Long boardId) {
        boardService.startTest(boardId, authUserDTO.getUserId());
        return new ResponseEntity<>("테스트 시작", HttpStatus.OK);
    }

    /**
     * 테스트 종료 (스케줄러가 수행하게 되어 사용하지 않음)
     */
    @PostMapping("/{boardId}/end")
    public ResponseEntity<String> endTest(@AuthUser AuthUserDTO authUserDTO,
                                          @PathVariable Long boardId) {
        boardService.endTest(boardId, authUserDTO.getUserId());
        return new ResponseEntity<>("테스트 종료", HttpStatus.OK);
    }
}
