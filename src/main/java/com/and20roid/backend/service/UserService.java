package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_COMPLETED;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Authority;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AuthorityRepository;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.ReadUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BoardRepository boardRepository;
    private final ParticipationRepository participationRepository;

    public String signup(String uid, String nickname) {
        log.info("signup by uid[{}], nickname[{}]", uid, nickname);

        if (userRepository.existsByUid(uid)) {
            throw new CustomException(CommonCode.ALREADY_EXIST_USER);
        }

        User savedUser = userRepository.save(new User(uid, nickname));
        authorityRepository.save(new Authority(savedUser, ROLE_USER));

        return "성공";
    }

    public ReadUserResponse readUser(Long userId) {
        int uploadBoardCount = boardRepository.countByUserId(userId);
        int completedTestCount = participationRepository.countByUserIdAndStatus(userId, BOARD_PARTICIPATION_COMPLETED);

        return new ReadUserResponse(completedTestCount, uploadBoardCount);
    }
}
