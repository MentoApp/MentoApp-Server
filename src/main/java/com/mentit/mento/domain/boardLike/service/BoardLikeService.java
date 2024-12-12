package com.mentit.mento.domain.boardLike.service;

import com.mentit.mento.domain.boardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.boardLike.service.port.BoardLikeRepository;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.BoardException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.redis.service.RedisLikeService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
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
        List<BoardEntity> boards = boardEntityRepository.findAll(); // 모든 게시판 조회
        for (BoardEntity board : boards) {
            long likeCount = boardLikeRepository.countByBoard(board); // 게시판별 좋아요 수 카운트
            redisLikeService.setLikeCount(board.getBoardId(), likeCount); // Redis에 저장
        }
    }

    public Long like(CustomUserDetail customUserDetail, Long boardId) {
        UsersEntity findUserByUserDetail = getUser(customUserDetail);

        BoardEntity findBoard = boardEntityRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
        Optional<BoardLikeEntity> curBoardLikeEntity = boardLikeRepository.findBoardLikeByBoardAndUsersEntity(findBoard, findUserByUserDetail);

        //이미 좋아요 엔티티가 존재했을때 요청할 경우, 엔티티를 삭제하고 하나 차감
        if (curBoardLikeEntity.isPresent()) {
            BoardLikeEntity findBoardLikeEntity = curBoardLikeEntity.get();
            findBoardLikeEntity = findBoardLikeEntity.toBuilder().liked(!findBoardLikeEntity.getLiked()).build();
            boardLikeRepository.delete(findBoardLikeEntity);
            redisLikeService.decrementLikeCount(boardId);
            return redisLikeService.getLikeCount(boardId);
        }

        BoardLikeEntity createdBoardLikeEntity = BoardLikeEntity.builder()
                .user(findUserByUserDetail)
                .liked(true)
                .boardEntity(findBoard)
                .build();

        createdBoardLikeEntity= boardLikeRepository.save(createdBoardLikeEntity);

        redisLikeService.incrementLikeCount(boardId); // Redis 좋아요 수 증가


        return redisLikeService.getLikeCount(boardId);

    }

    private UsersEntity getUser(CustomUserDetail customUserDetail) {

        return userRepository.findById(customUserDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}
