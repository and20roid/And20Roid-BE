package com.and20roid.backend.service;

import com.and20roid.backend.common.exception.CustomException;
import com.and20roid.backend.common.response.CommonCode;
import com.and20roid.backend.entity.AppIntroductionImage;
import com.and20roid.backend.entity.Board;
import com.and20roid.backend.entity.User;
import com.and20roid.backend.repository.AppIntroductionImageRepository;
import com.and20roid.backend.repository.BoardRepository;
import com.and20roid.backend.repository.UserRepository;
import com.and20roid.backend.vo.CreateBoardRequest;
import com.and20roid.backend.vo.ReadBoardInfoResponse;
import com.and20roid.backend.vo.ReadBoardsResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AppIntroductionImageRepository appIntroductionImageRepository;
    private final AwsS3Service awsS3Service;

    public void createBoard(CreateBoardRequest createBoardRequest, MultipartFile thumbnailFile, List<MultipartFile> multipartFiles, String uid)
            throws FileUploadException {
        String thumbnailUrl = awsS3Service.uploadFile(thumbnailFile);

        User user = userRepository.findByUid(uid)
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

    public ReadBoardsResponse readBoards(Long lastBoardId, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(0, pageSize, sort);

        Page<Board> page = null;

        if (lastBoardId == null || lastBoardId < 1) {
            page = boardRepository.findAll(pageRequest);
        } else {
            page = boardRepository.findByIdLessThan(lastBoardId, pageRequest);
        }

        return new ReadBoardsResponse(page.getContent());

    }

    public ReadBoardInfoResponse readBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CommonCode.NONEXISTENT_BOARD));

        List<AppIntroductionImage> appIntroductionImages = appIntroductionImageRepository.findAllByBoard(board);

        if (appIntroductionImages == null || appIntroductionImages.isEmpty()) {
            throw new CustomException(CommonCode.ZERO_INTRODUCTION_IMAGES);
        }

        List<String> imageUrls = appIntroductionImages.stream()
                .map(AppIntroductionImage::getUrl)
                .toList();

        return new ReadBoardInfoResponse(board, imageUrls);
    }
}
