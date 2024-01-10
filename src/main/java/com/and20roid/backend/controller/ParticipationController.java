package com.and20roid.backend.controller;

import com.and20roid.backend.service.ParticipationService;
import com.and20roid.backend.vo.CreateParticipationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("")
    public ResponseEntity createParticipation(@RequestBody CreateParticipationRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        participationService.createParticipation(request.getBoardId(), userDetails.getUsername());

        return new ResponseEntity("성공", HttpStatus.OK);
    }


}
