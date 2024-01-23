package com.and20roid.backend.scheduler;

import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PROCEEDING;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_END_UPLOADER;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_START;

import com.and20roid.backend.entity.Board;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.service.FcmService;
import com.and20roid.backend.vo.CreateMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final BoardRepository boardRepository;
    private final FcmService fcmService;
    private final MessageSource messageSource;

    @Scheduled(cron = "* * 12 * * *")
    public void sendFcmAboutTerminated() {
        log.info("start sendFcmAboutTerminated by scheduler");
        LocalDateTime twoWeeksAgoFromNow = LocalDateTime.now().minusDays(14);

        List<Board> boards = boardRepository.findAllByStateAndStartTimeIsBeforeAndFcmSentBySchedulerIsFalse(BOARD_STATE_PROCEEDING, twoWeeksAgoFromNow);

        if (boards != null && !boards.isEmpty()) {
            for (Board board : boards) {
                log.info("Send Fcm by scheduler to boardId: [{}]", board.getId());
                fcmService.sendMessageByToken(new CreateMessage(board.getUser().getId(),
                        messageSource.getMessage("TITLE_004", null, Locale.getDefault()),
                        messageSource.getMessage("CONTENT_004", new String[]{board.getTitle()}, Locale.getDefault()),
                        MESSAGE_TYPE_END_UPLOADER,
                        board));

                board.updateFcmSentByScheduler(true);
                boardRepository.save(board);
            }
        }
    }
}
