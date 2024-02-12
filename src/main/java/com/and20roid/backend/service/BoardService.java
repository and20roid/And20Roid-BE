package com.and20roid.backend.service;

import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_COMPLETED;
import static com.and20roid.backend.common.constant.Constant.BOARD_PARTICIPATION_IN_PROGRESS;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_END;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_OPEN;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PENDING;
import static com.and20roid.backend.common.constant.Constant.BOARD_STATE_PROCEEDING;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_END_TESTER;
import static com.and20roid.backend.common.constant.Constant.MESSAGE_TYPE_START;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.AppIntroductionImage;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.BoardLikeStatus;
import com.and20roid.backend.entity.ParticipationStatus;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AppIntroductionImageRepository;
import com.and20roid.backend.repository.BoardLikeStatusRepository;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.FcmMessageRepository;
import com.and20roid.backend.repository.ParticipationInviteStatusRepository;
import com.and20roid.backend.repository.ParticipationStatusRepository;
import com.and20roid.backend.repository.UserInteractionStatusRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.CreateBoardRequest;
import com.and20roid.backend.vo.CreateMessageRequest;
import com.and20roid.backend.vo.ReadBoardInfoResponse;
import com.and20roid.backend.vo.ReadBoardQuery;
import com.and20roid.backend.vo.ReadBoardsResponse;
import com.and20roid.backend.vo.UpdateBoardRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AppIntroductionImageRepository appIntroductionImageRepository;
    private final BoardLikeStatusRepository boardLikeStatusRepository;
    private final ParticipationStatusRepository participationStatusRepository;
    private final AwsS3Service awsS3Service;
    private final FcmService fcmService;
    private final MessageSource messageSource;

    @Transactional
    public void createBoard(CreateBoardRequest createBoardRequest, MultipartFile thumbnailFile, List<MultipartFile> multipartFiles, long userId)
            throws FileUploadException {
        log.info("start createBoard by userId: [{}], title: [{}]", userId, createBoardRequest.getTitle());

        if (multipartFiles.size() > 3) {
            throw new CustomException(CommonCode.TOO_MANY_IMAGES);
        }

        String thumbnailUrl = awsS3Service.uploadFile(thumbnailFile);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        Board savedBoard = boardRepository.save(new Board(createBoardRequest, thumbnailUrl, user));

        List<String> urls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            urls.add(awsS3Service.uploadFile(multipartFile));
        }

        List<AppIntroductionImage> collect = urls.stream()
                .map(url -> new AppIntroductionImage(savedBoard, url))
                .toList();

        appIntroductionImageRepository.saveAll(collect);
    }

    public ReadBoardsResponse readBoards(Long lastBoardId, int pageSize, Long userId) {
        log.info("start readBoards by lastBoardId: [{}], userId: [{}]", lastBoardId, userId);

        List<ReadBoardQuery> readBoardsResponse;

        if (lastBoardId == null || lastBoardId < 1) {
            readBoardsResponse = boardRepository.findReadBoardsResponse(userId, pageSize);
        } else {
            readBoardsResponse = boardRepository.findReadBoardsResponse(userId, lastBoardId, pageSize);
        }

        return new ReadBoardsResponse(readBoardsResponse);
    }

    public ReadBoardInfoResponse readBoard(Long boardId, Long userId) {
        log.info("start readBoard by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        if (board.isDeleted()) {
            throw new CustomException(CommonCode.DELETED_BOARD);
        }

        ReadBoardQuery readBoardQuery = boardRepository.findReadBoardResponse(userId, boardId);

//        List<AppIntroductionImage> appIntroductionImages = appIntroductionImageRepository.findAllByBoard(board);
//
//        if (appIntroductionImages == null || appIntroductionImages.isEmpty()) {
//            throw new CustomException(CommonCode.ZERO_INTRODUCTION_IMAGES);
//        }
//
//        List<String> imageUrls = appIntroductionImages.stream()
//                .map(AppIntroductionImage::getUrl)
//                .toList();

        Board updatedBoard = boardRepository.save(board.addViews());

        return new ReadBoardInfoResponse(updatedBoard, readBoardQuery);
    }

    @Transactional
    public String updateBoardLikes(Long boardId, long userId) {
        log.info("start updateBoardLikes by boardId: [{}], userId: [{}]", boardId, userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_USER));

        String msg = "";

        if (boardLikeStatusRepository.existsByBoardIdAndUserId(boardId, userId)) {
            boardLikeStatusRepository.deleteByBoardIdAndUserId(boardId, userId);
            board.cancelLikes();
            boardRepository.save(board);
            msg = "좋아요 취소";
        } else {
            boardLikeStatusRepository.save(new BoardLikeStatus(user, board));
            board.addLikes();
            boardRepository.save(board);
            msg = "좋아요";
        }

        return msg;
    }

    public void startTest(Long boardId, long userId) {
        log.info("start startTest by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        // 작성자가 아닌 경우 예외처리
        if (board.getUser().getId() != userId) {
            throw new CustomException(CommonCode.FORBIDDEN_BOARD);
        }

        // 이미 진행 중이거나, 종료된 테스트 예외처리
        if (!(board.getState().equals(BOARD_STATE_OPEN) || board.getState().equals(BOARD_STATE_PENDING))) {
            throw new CustomException(CommonCode.INVALID_BOARD_STATE);
        }

        List<ParticipationStatus> participationStatuses = participationStatusRepository.findByBoardId(boardId);

        // fcm 전송
        participationStatuses.stream()
                .map(ParticipationStatus::getUser)
                .forEach(user -> fcmService.sendMessage(new CreateMessageRequest(user.getId(), null,
                                messageSource.getMessage("TITLE_003", null, Locale.getDefault()),
                                messageSource.getMessage("CONTENT_003", new String[]{board.getTitle()}, Locale.getDefault()),
                                MESSAGE_TYPE_START,
                                board)
                        )
                );

        // 상태 변경
        participationStatuses
                        .forEach(participationStatus -> participationStatus.updateStatus(BOARD_PARTICIPATION_IN_PROGRESS));

        participationStatusRepository.saveAll(participationStatuses);

        board.updateStatus(BOARD_STATE_PROCEEDING);
        board.updateStartTime();
        board.updateFcmSentByScheduler(false);

        boardRepository.save(board);
    }

    public void endTest(Long boardId, long userId) {
        log.info("start endTest by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        // 작성자가 아닌 경우 예외처리
        if (board.getUser().getId() != userId) {
            throw new CustomException(CommonCode.FORBIDDEN_BOARD);
        }

        // 진행 중이 아닌 경우 예외처리
        if (!(board.getState().equals(BOARD_STATE_PROCEEDING))) {
            throw new CustomException(CommonCode.INVALID_BOARD_STATE);
        }

        List<ParticipationStatus> participationStatuses = participationStatusRepository.findByBoardId(boardId);

        // fcm 전송
        participationStatuses.stream()
                .map(ParticipationStatus::getUser)
                .forEach(user -> fcmService.sendMessage(new CreateMessageRequest(user.getId(), null,
                                messageSource.getMessage("TITLE_005", null, Locale.getDefault()),
                                messageSource.getMessage("CONTENT_005", new String[]{board.getTitle()}, Locale.getDefault()),
                                MESSAGE_TYPE_END_TESTER,
                                board)
                        )
                );

        // 상태 변경
        participationStatuses
                .forEach(participationStatus -> participationStatus.updateStatus(BOARD_PARTICIPATION_COMPLETED));

        participationStatusRepository.saveAll(participationStatuses);

        board.updateStatus(BOARD_STATE_END);
        board.updateEndTime();

        boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(Long boardId, long userId, UpdateBoardRequest request, MultipartFile thumbnailFile, List<MultipartFile> multipartFiles)
            throws FileUploadException {
        log.info("start updateBoard by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        // 작성자가 아닌 경우 예외처리
        if (board.getUser().getId() != userId) {
            throw new CustomException(CommonCode.FORBIDDEN_BOARD);
        }

        List<AppIntroductionImage> appIntroductionImages = appIntroductionImageRepository.findAllByBoard(board);

        Set<String> imageUrlSet = appIntroductionImages.stream()
                .map(AppIntroductionImage::getUrl)
                .collect(Collectors.toSet());

        Set<String> deleteImageUrls = new HashSet<>(request.getDeleteImageUrls());

        // 삭제하고자 하는 image url이 존재하지 않는 경우 예외처리
        for (String deleteImageUrl : deleteImageUrls) {
            if (!imageUrlSet.contains(deleteImageUrl)) {
                throw new CustomException(CommonCode.NONEXISTENT_BOARD_IMAGE);
            }
        }

        // 이미지 3개 넘어가면 예외처리
        if (appIntroductionImages.size() + multipartFiles.size() - deleteImageUrls.size() > 3) {
            throw new CustomException(CommonCode.TOO_MANY_IMAGES);
        }

        // 이미지 삭제
        appIntroductionImageRepository.deleteByBoardIdAndImageUrls(boardId, request.getDeleteImageUrls());

        // 썸네일 변경
        if (!thumbnailFile.isEmpty()) {
            String updatedThumbnailUrl = awsS3Service.uploadFile(thumbnailFile);
            board.updateThumbnailUrl(updatedThumbnailUrl);
        }

        board.update(request);

        Board updatedBoard = boardRepository.save(board);

        // 이미지 추가
        List<String> urls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            urls.add(awsS3Service.uploadFile(multipartFile));
        }

        List<AppIntroductionImage> collect = urls.stream()
                .map(url -> new AppIntroductionImage(updatedBoard, url))
                .toList();

        appIntroductionImageRepository.saveAll(collect);
    }

    @Transactional
    public void deleteBoard(Long boardId, long userId) {
        log.info("start deleteBoard by boardId: [{}], userId: [{}]", boardId, userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        // 작성자가 아닌 경우 예외처리
        if (board.getUser().getId() != userId) {
            throw new CustomException(CommonCode.FORBIDDEN_BOARD);
        }

        appIntroductionImageRepository.deleteAllByBoardId(boardId);
        board.delete();
        boardRepository.save(board);
    }
}
