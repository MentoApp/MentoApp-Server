package com.mentit.mento.domain.BoardLike.service;

import com.mentit.mento.domain.BoardLike.entity.BoardLike;
import com.mentit.mento.domain.BoardLike.repository.BoardLikeRepository;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.repository.BoardRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.UserRepositoryImpl;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.BoardException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.redis.service.RedisLikeService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardLikeService {
    private final UserRepositoryImpl userRepositoryImpl;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final RedisLikeService redisLikeService;

    @PostConstruct
    public void init() {
        initializeLikeCounts();
    }

    private void initializeLikeCounts() {
        List<Board> boards = boardRepository.findAll(); // 모든 게시판 조회
        for (Board board : boards) {
            long likeCount = boardLikeRepository.countByBoard(board); // 게시판별 좋아요 수 카운트
            redisLikeService.setLikeCount(board.getBoardId(), likeCount); // Redis에 저장
        }
    }

    public Long like(CustomUserDetail customUserDetail, Long boardId) {
        UsersEntity findUserByUserDetail = getUser(customUserDetail);

        Board findBoardByBoardId = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
        Optional<BoardLike> existingBoardLike = boardLikeRepository.findBoardLikeByBoardAndUser(findBoardByBoardId, findUserByUserDetail);

        if (existingBoardLike.isPresent()) {
            BoardLike findBoardLike = existingBoardLike.get();
            BoardLike updatedBoardLike = findBoardLike.toBuilder().liked(!findBoardLike.getLiked()).build();
            boardLikeRepository.save(updatedBoardLike);
            return boardId;
        }

        BoardLike createdBoardLike = BoardLike.builder()
                .user(findUserByUserDetail)
                .liked(true)
                .board(findBoardByBoardId)
                .build();

        boardLikeRepository.save(createdBoardLike);

        if (createdBoardLike.getLiked()) {
            redisLikeService.incrementLikeCount(boardId); // Redis 좋아요 수 증가
        }else{
            redisLikeService.decrementLikeCount(boardId);
        }

        return redisLikeService.getLikeCount(boardId);

    }

//    public void disLike(CustomUserDetail customUserDetail, Long boardId) {
//        Users findUserByUserDetail = getUser(customUserDetail);
//
//        Board findBoardByBoardId = boardRepository.findByBoardId(boardId).orElseThrow(
//                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
//        );
//
//
//        BoardLike findBoardLike = boardLikeRepository.findBoardLikeByBoardAndUser(findBoardByBoardId, findUserByUserDetail).orElseThrow(
//                () -> new BoardLikeException(ExceptionCode.NOT_FOUND_BOARD_LIKE)
//        );
//        BoardLike updatedBoardLike = findBoardLike.toBuilder()
//                .liked(false)
//                .build();
//
//        boardLikeRepository.save(updatedBoardLike);
//    }

    private UsersEntity getUser(CustomUserDetail customUserDetail) {

        return userRepositoryImpl.findById(customUserDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}
