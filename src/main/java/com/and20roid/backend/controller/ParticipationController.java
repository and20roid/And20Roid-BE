package com.and20roid.backend.controller;

import static com.and20roid.backend.common.constant.Constant.DEFAULT_ONE_PAGE_SIZE;

import com.and20roid.backend.service.ParticipationService;
import com.and20roid.backend.vo.CreateParticipateInviteRequest;
import com.and20roid.backend.vo.CreateParticipationRequest;
import com.and20roid.backend.vo.ReadBoardWithInviteInfosResponse;
import com.and20roid.backend.vo.ReadParticipantsResponse;
import com.and20roid.backend.vo.ReadRankingResponse;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 모집글 참여
     */
    @PostMapping("")
    public ResponseEntity<String> createParticipation(@RequestBody CreateParticipationRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        participationService.createParticipation(request.getBoardId(), request.getEmail(), userId);

        return new ResponseEntity<>("성공", HttpStatus.OK);
    }

    /**
     * 랭킹 조회
     */
    @GetMapping("/ranking")
    public ResponseEntity<ReadRankingResponse> readParticipation() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(participationService.readRanking(userId), HttpStatus.OK);
    }

    /**
     * 테스트 참여 요청 모집글 리스트 조회
     */
    @GetMapping("/invite/{userId}")
    public ResponseEntity<ReadBoardWithInviteInfosResponse> readBoardWithInviteInfos(@PathVariable(name = "userId") Long invitedUserId,
                                                                                     @Nullable @RequestParam Long lastBoardId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(participationService.readBoardWithInviteInfos(invitedUserId, userId, lastBoardId, DEFAULT_ONE_PAGE_SIZE), HttpStatus.OK);
    }

    /**
     * 테스트 참여 요청
     */
    @PostMapping("/invite/{userId}")
    public ResponseEntity<String> createParticipateInvite(@PathVariable(name = "userId") Long invitedUserId,
                                                          @RequestBody CreateParticipateInviteRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = Long.parseLong(userDetails.getUsername());

        participationService.createParticipateInvite(request.getBoardId(), userId, invitedUserId);

        return new ResponseEntity<>("성공", HttpStatus.OK);
    }

    /**
     * 모집원 보기
     */
    @GetMapping("/{boardId}/participants")
    public ResponseEntity<ReadParticipantsResponse> readParticipants(@PathVariable Long boardId) {
        return new ResponseEntity<>(participationService.readParticipants(boardId), HttpStatus.OK);
    }
}
