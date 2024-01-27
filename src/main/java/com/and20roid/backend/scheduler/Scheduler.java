package com.and20roid.backend.scheduler;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_COMPLETED;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_END;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PROCEEDING;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_END_TESTER;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_END_UPLOADER;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.UserInteractionStatus;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.repository.UserInteractionStatusRepository;
import com.and20roid.backend.service.FcmService;
import com.and20roid.backend.vo.CreateMessageRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final ParticipationStatusRepository participationStatusRepository;

    private final BoardRepository boardRepository;
    private final FcmService fcmService;
    private final MessageSource messageSource;
    private final UserInteractionStatusRepository userInteractionStatusRepository;

    @Scheduled(cron = "0 0 0/1 * * *")
    @Transactional
    public void sendFcmAboutTerminated() {
        log.info("start sendFcmAboutTerminated by scheduler");
        LocalDateTime twoWeeksAgoFromNow = LocalDateTime.now().minusDays(14);

        List<Board> boards = boardRepository.findAllByStateAndStartTimeIsBeforeAndFcmSentBySchedulerIsFalse(BOARD_STATE_PROCEEDING, twoWeeksAgoFromNow);

        if (boards != null && !boards.isEmpty()) {
            for (Board board : boards) {
                log.info("Send Fcm by scheduler to boardId: [{}]", board.getId());

                List<ParticipationStatus> participationStatuses = participationStatusRepository.findByBoardId(board.getId());

                board.updateStatus(BOARD_STATE_END);
                board.updateFcmSentByScheduler(true);
                board.updateEndTime();
                boardRepository.save(board);

                if (participationStatuses != null && !participationStatuses.isEmpty()) {
                    // 참여 내역 상태변경 (테스트_진행중 -> 테스트_완료)
                    participationStatusRepository.saveAll(participationStatuses.stream()
                            .map(participationStatus -> participationStatus.updateStatus(
                                    BOARD_PARTICIPATION_COMPLETED))
                            .collect(Collectors.toList()));

                    // 상호작용 DB에 저장
                    userInteractionStatusRepository.saveAll(participationStatuses.stream()
                            .map(participationStatus -> new UserInteractionStatus(board.getUser(),
                                    participationStatus.getUser(), board)).toList());

                    // fcm 전송 (for tester)
                    participationStatuses.stream()
                            .map(ParticipationStatus::getUser)
                            .forEach(user -> fcmService.sendMessage(new CreateMessageRequest(user.getId(),
                                            messageSource.getMessage("TITLE_005", null, Locale.getDefault()),
                                            messageSource.getMessage("CONTENT_005", new String[]{board.getTitle()}, Locale.getDefault()),
                                            MESSAGE_TYPE_END_TESTER,
                                            board)
                                    )
                            );
                }

                // fcm 전송 (for uploader)
                fcmService.sendMessage(new CreateMessageRequest(board.getUser().getId(),
                        messageSource.getMessage("TITLE_004", null, Locale.getDefault()),
                        messageSource.getMessage("CONTENT_004", new String[]{board.getTitle()}, Locale.getDefault()),
                        MESSAGE_TYPE_END_UPLOADER,
                        board));
            }
        }
    }
}
