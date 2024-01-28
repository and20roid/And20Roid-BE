package com.and20roid.backend.repository;

import com.and20roid.backend.entity.Board;
import com.and20roid.backend.vo.ReadBoardQuery;
import com.and20roid.backend.vo.ReadBoardWithInviteInfoQuery;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByIdLessThan(Long lastBoardId, PageRequest pageRequest);

    int countByUserId(Long userId);

    Page<Board> findByIdLessThanAndUserId(Long lastBoardId, Long userId, PageRequest pageRequest);

    Page<Board> findByUserId(Long userId, PageRequest pageRequest);

    List<Board> findAllByUserId(Long userId);

    Page<Board> findByIdIn(List<Long> boardIdList, PageRequest pageRequest);

    Page<Board> findByIdLessThanAndIdIn(Long lastBoardId, List<Long> boardIdList, PageRequest pageRequest);

//    @Query(value = "with board_with_introduction_image as (select b.id, GROUP_CONCAT(a.url) as urls from app_introduction_image a inner join board b on a.board_id = b.id group by b.id), "
//            + "like_board_calc as (select b.*, if(l.id IS NOT NULL, 1, 0) as is_liked_board from board b left join board_like_status l on l.board_id = b.id where l.user_id = :userId), "
//            + "like_board_calc_with_user as (select l.*, nickname from like_board_calc l inner join user u on l.user_id = u.id) "
//            + "select b.id as id, title, urls as imageUrls, participant_num as participantNum, recruitment_num as recruitmentNum, created_date as createdDate, state, thumbnail_url as thumbnailUrl, views, likes, is_liked_board as isLikedBoard, nickname, intro_line as introLine from board_with_introduction_image b inner join like_board_calc_with_user l on b.id = l.id "
//            + "where b.id < :lastBoardId "
//            + "order by b.id desc "
//            + "limit :pageSize ", nativeQuery = true)
//    List<ReadBoardQuery> findReadBoardsResponse(Long userId, Long lastBoardId, int pageSize);

    @Query(value = "with board_with_introduction_image as (select b.id, GROUP_CONCAT(a.url) as urls from app_introduction_image a inner join board b on a.board_id = b.id where b.is_deleted is false group by b.id), "
            + "board_with_user as (select b.*, nickname, u.id as upload_user_id from board b inner join user u on b.user_id = u.id), "
            + "like_board_calc as (select * from board_like_status where user_id = :userId), "
            + "board_with_user_like_board_calc as (select b.*, if(l.id IS NOT NULL, 1, 0) as is_liked_board from board_with_user b left join like_board_calc l on b.id = l.board_id)"
            + "select b.id as id, title, urls as imageUrls, participant_num as participantNum, recruitment_num as recruitmentNum, created_date as createdDate, state, thumbnail_url as thumbnailUrl, views, likes, is_liked_board as isLikedBoard, nickname, intro_line as introLine, if(l.upload_user_id = :userId, 1, 0) as isMine from board_with_introduction_image b inner join board_with_user_like_board_calc l on b.id = l.id "
            + "where b.id < :lastBoardId "
            + "order by b.id desc "
            + "limit :pageSize ", nativeQuery = true)
    List<ReadBoardQuery> findReadBoardsResponse(Long userId, Long lastBoardId, int pageSize);

    @Query(value = "with board_with_introduction_image as (select b.id, GROUP_CONCAT(a.url) as urls from app_introduction_image a inner join board b on a.board_id = b.id where b.is_deleted is false group by b.id), "
            + "board_with_user as (select b.*, nickname, u.id as upload_user_id, user from board b inner join user u on b.user_id = u.id), "
            + "like_board_calc as (select * from board_like_status where user_id = :userId), "
            + "board_with_user_like_board_calc as (select b.*, if(l.id IS NOT NULL, 1, 0) as is_liked_board from board_with_user b left join like_board_calc l on b.id = l.board_id)"
            + "select b.id as id, title, urls as imageUrls, participant_num as participantNum, recruitment_num as recruitmentNum, created_date as createdDate, state, thumbnail_url as thumbnailUrl, views, likes, is_liked_board as isLikedBoard, nickname, intro_line as introLine, if(l.upload_user_id = :userId, 1, 0) as isMine from board_with_introduction_image b inner join board_with_user_like_board_calc l on b.id = l.id "
            + "order by b.id desc "
            + "limit :pageSize ", nativeQuery = true)
    List<ReadBoardQuery> findReadBoardsResponse(Long userId, int pageSize);

    @Query(value = "with board_with_introduction_image as (select b.id, GROUP_CONCAT(a.url) as urls from app_introduction_image a inner join board b on a.board_id = b.id group by b.id), "
            + "board_with_user as (select b.*, nickname from board b inner join user u on b.user_id = u.id), "
            + "like_board_calc as (select * from board_like_status where user_id = :userId), "
            + "board_with_user_like_board_calc as (select b.*, if(l.id IS NOT NULL, 1, 0) as is_liked_board from board_with_user b left join like_board_calc l on b.id = l.board_id)"
            + "select b.id as id, title, urls as imageUrls, participant_num as participantNum, recruitment_num as recruitmentNum, created_date as createdDate, state, thumbnail_url as thumbnailUrl, views, likes, is_liked_board as isLikedBoard, nickname, intro_line as introLine from board_with_introduction_image b inner join board_with_user_like_board_calc l on b.id = l.id "
            + "where b.id = :boardId ", nativeQuery = true)
    ReadBoardQuery findReadBoardResponse(Long userId, Long boardId);

    @Query(value = "with uploader_board as (select b.* from board b inner join user u on b.user_id = u.id where u.id = :userId), "
            + "invite_history as (select * from participation_invite_status where user_id = :invitedUserId) "
            + "select ub.id as id, title, participant_num as participantNum, recruitment_num as recruitmentNum, ub.created_date as createdDate, state, thumbnail_url as thumbnailUrl, intro_line as introLine, if(i.user_id is null, 0, 1) as isAlreadyInvited "
            + "from uploader_board ub left join invite_history i on ub.id = i.board_id "
            + "where ub.id < :lastBoardId "
            + "order by ub.id desc "
            + "limit :pageSize", nativeQuery = true)
    List<ReadBoardWithInviteInfoQuery> findBoardsWithInviteInfoByUserId(Long invitedUserId, Long userId, Long lastBoardId, int pageSize);

    @Query(value = "with uploader_board as (select b.* from board b inner join user u on b.user_id = u.id where u.id = :userId), "
            + "invite_history as (select * from participation_invite_status where user_id = :invitedUserId) "
            + "select ub.id as id, title, participant_num as participantNum, recruitment_num as recruitmentNum, ub.created_date as createdDate, state, thumbnail_url as thumbnailUrl, intro_line as introLine, if(i.user_id is null, 0, 1) as isAlreadyInvited "
            + "from uploader_board ub left join invite_history i on ub.id = i.board_id "
            + "order by ub.id desc "
            + "limit :pageSize", nativeQuery = true)
    List<ReadBoardWithInviteInfoQuery> findBoardsWithInviteInfoByUserId(Long invitedUserId, Long userId, int pageSize);

    List<Board> findAllByStateAndStartTimeIsBeforeAndFcmSentBySchedulerIsFalse(String state, LocalDateTime twoWeeksAgoFromNow);

}
