package com.mentit.mento.domain.BoardLike.service;

import com.mentit.mento.domain.BoardLike.domain.BoardLike;
import com.mentit.mento.domain.BoardLike.service.port.BoardLikeRepository;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.service.port.UserRepository;
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
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardEntityRepository;
    private final RedisLikeService redisLikeService;

    @PostConstruct
    public void init() {
        initializeLikeCounts();
    }

    private void initializeLikeCounts() {
        List<Board> boards = boardEntityRepository.findAll(); // 모든 게시판 조회
        for (Board board : boards) {
            long likeCount = boardLikeRepository.countByBoard(board); // 게시판별 좋아요 수 카운트
            redisLikeService.setLikeCount(board.getBoardId(), likeCount); // Redis에 저장
        }
    }

    public Long like(CustomUserDetail customUserDetail, Long boardId) {
        Users findUserByUserDetail = getUser(customUserDetail);

        Board findBoardByBoardEntityId = boardEntityRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
        Optional<BoardLike> existingBoardLike = boardLikeRepository.findBoardLikeByBoardAndUsersEntity(findBoardByBoardEntityId, findUserByUserDetail);

        if (existingBoardLike.isPresent()) {
            BoardLike findBoardLikeEntity = existingBoardLike.get();
            BoardLike updatedBoardLike = findBoardLikeEntity.toBuilder().liked(!findBoardLikeEntity.getLiked()).build();
            boardLikeRepository.save(updatedBoardLike);
            return boardId;
        }

        BoardLike createdBoardLikeEntity = BoardLike.builder()
                .user(findUserByUserDetail)
                .liked(true)
                .board(findBoardByBoardEntityId)
                .build();

        boardLikeRepository.save(createdBoardLikeEntity);

        if (createdBoardLikeEntity.getLiked()) {
            redisLikeService.incrementLikeCount(boardId); // Redis 좋아요 수 증가
        }else{
            redisLikeService.decrementLikeCount(boardId);
        }

        return redisLikeService.getLikeCount(boardId);

    }

    private Users getUser(CustomUserDetail customUserDetail) {

        return userRepository.findById(customUserDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}
