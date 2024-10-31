package com.mentit.mento.domain.board.service;

import com.mentit.mento.domain.BoardLike.service.BoardLikeService;
import com.mentit.mento.domain.board.dto.*;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.entity.BoardFiles;
import com.mentit.mento.domain.board.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.repository.BoardFileRepository;
import com.mentit.mento.domain.board.repository.BoardKeywordForCreatingEntityRepository;
import com.mentit.mento.domain.board.repository.BoardRepository;
import com.mentit.mento.domain.comment.dto.CommentsResponse;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.comment.repository.CommentRepository;
import com.mentit.mento.domain.comment.service.CommentService;
import com.mentit.mento.domain.users.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.BoardKeywordRepository;
import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.BoardException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.redis.service.RedisLikeService;
import com.mentit.mento.global.s3.S3FileUtilImpl;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final UserRepository userRepository;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardKeywordForCreatingEntityRepository boardKeywordForCreatingEntityRepository;
    private final CommentRepository commentRepository;
    private final RedisLikeService redisLikeService;
    private final CommentService commentService;

    @Transactional
    public void createBoard(CustomUserDetail customUserDetail, BoardCreateRequest boardCreateRequest, List<MultipartFile> images) {
        Users findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardCreateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        Board savedboard = createBoard(boardCreateRequest, findUserByUserDetail);

        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(boardCreateRequest, savedboard);

        List<BoardFiles> boardFiles = getBoardFiles(images, savedboard);

        mappingBoardFileAndBoardKeywordInSavedBoard(savedboard, boardFiles, boardKeywordForCreatingEntityList);

    }

    @Transactional
    public void updateBoard(CustomUserDetail customUserDetail, BoardUpdateRequest boardUpdateRequest, List<MultipartFile> images) {
        Users findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardUpdateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }
        Board findBoard = boardRepository.findByBoardId(boardUpdateRequest.getBoardId()).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );

        boardKeywordForCreatingEntityRepository.deleteAllByBoard(findBoard);

        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(boardUpdateRequest, findBoard);

        Board savedboard = updateBoard(boardUpdateRequest, findBoard);

        List<BoardFiles> boardList = findBoard.getBoardFiles();

        if (boardList != null && !boardList.isEmpty()) {
            boardFileRepository.deleteAllByBoard(findBoard);
        }

        List<BoardFiles> boardFiles = getBoardFiles(images, savedboard);

        mappingBoardFileAndBoardKeywordInSavedBoard(findBoard, boardFiles, boardKeywordForCreatingEntityList);

    }

    @Transactional
    public void deleteBoard(CustomUserDetail customUserDetail, Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_BOARD)
        );

        List<BoardFiles> boardFiles = boardFileRepository.findAllByBoard(findBoard);
        boardFiles.forEach(
                boardFile -> {
                    s3FileUtilImpl.deleteImageFromS3(boardFile.getBoardFileName());
                }
        );

        commentRepository.deleteAllByBoard(findBoard);

        boardFileRepository.deleteAllByBoard(findBoard);

        boardKeywordForCreatingEntityRepository.deleteAllByBoard(findBoard);

        boardRepository.delete(findBoard);

    }

    private void mappingBoardFileAndBoardKeywordInSavedBoard(Board savedboard, List<BoardFiles> boardFiles, List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList) {
        Board updatedSavedBoard = savedboard.toBuilder()
                .boardFiles(boardFiles)
                .boardKeywordForCreatings(boardKeywordForCreatingEntityList)
                .build();

        boardRepository.save(updatedSavedBoard);
    }

    private Board createBoard(BoardCreateRequest boardCreateRequest, Users findUserByUserDetail) {
        Board createdBoard = Board.builder()
                .title(boardCreateRequest.getTitle())
                .content(boardCreateRequest.getContent())
                .writer(findUserByUserDetail)
                .boardType(boardCreateRequest.getBoardType())
                .viewCount(1L)
                .build();

        return boardRepository.save(createdBoard);
    }

    private Board updateBoard(BoardUpdateRequest boardUpdateRequest, Board curBoard) {
        Board createdBoard = curBoard.toBuilder()
                .title(boardUpdateRequest.getTitle())
                .content(boardUpdateRequest.getContent())
                .boardType(boardUpdateRequest.getBoardType())
                .viewCount(1L)
                .build();

        return boardRepository.save(createdBoard);
    }

    private List<BoardKeywordForCreatingEntity> getBoardKeywordForCreatingEntities(BoardCreateRequest boardCreateRequest, Board savedboard) {
        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = new ArrayList<>();
        if (boardCreateRequest.getKeywords() != null) {

            boardCreateRequest.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreatingEntity savedBoardKeyword = BoardKeywordForCreatingEntity.builder()
                                .board(savedboard)
                                .boardKeyword(keyword)
                                .build();

                        BoardKeywordForCreatingEntity savedBoardKeywordForCreatingEntity = boardKeywordForCreatingEntityRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingEntityList.add(savedBoardKeywordForCreatingEntity);
                    }
            );
        }
        return boardKeywordForCreatingEntityList;
    }

    private List<BoardKeywordForCreatingEntity> getBoardKeywordForCreatingEntities(BoardUpdateRequest boardUpdateRequest, Board findBoard) {
        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = new ArrayList<>();
        if (boardUpdateRequest.getKeywords() != null) {

            boardUpdateRequest.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreatingEntity savedBoardKeyword = BoardKeywordForCreatingEntity.builder()
                                .board(findBoard)
                                .boardKeyword(keyword)
                                .build();

                        BoardKeywordForCreatingEntity savedBoardKeywordForCreatingEntity = boardKeywordForCreatingEntityRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingEntityList.add(savedBoardKeywordForCreatingEntity);
                    }
            );
        }
        return boardKeywordForCreatingEntityList;
    }


    private List<BoardFiles> getBoardFiles(List<MultipartFile> images, Board savedboard) {
        List<BoardFiles> boardFiles = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String uploadedUrl = s3FileUtilImpl.upload(image);
                BoardFiles createdFileEntity = BoardFiles.builder()
                        .boardFileName(uploadedUrl)
                        .board(savedboard)
                        .build();
                BoardFiles savedBoardFileEntity = boardFileRepository.save(createdFileEntity);
                boardFiles.add(savedBoardFileEntity);
            }
        }
        return boardFiles;
    }


    private Users getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public FindBoardResponse findBoard(CustomUserDetail customUserDetail, Long boardId) {
        Board findBoardByBoardId = boardRepository.findById(boardId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_BOARD)
        );

        // 게시판 키워드 생성
        List<String> keywords = getBoardKeywords(findBoardByBoardId);

        // 이미지 리스트 생성 (비어있을 경우 빈 리스트 반환)
        List<String> imageList = getImageList(findBoardByBoardId);

        //좋아요 카운트
        Long likeCount = redisLikeService.getLikeCount(boardId);

        //유저 정보
        UserInfoInBoardResponse userInfoInBoardResponse = getUserInfoInBoardResponse(findBoardByBoardId);

        //댓글 수
        Long commentCount = commentService.getCommentCount(boardId);

        return FindBoardResponse.builder()
                .title(findBoardByBoardId.getTitle())
                .writer(userInfoInBoardResponse.getNickname())
                .createdTime(findBoardByBoardId.getCreatedAt())
                .content(findBoardByBoardId.getContent())
                .viewCount(findBoardByBoardId.getViewCount())
                .imageList(imageList)
                .likeCount(likeCount)
                .viewCount(findBoardByBoardId.getViewCount() + 1L)
                .userInfo(userInfoInBoardResponse)
                .commentCount(commentCount)
                .boardKeywords(keywords)
                .build();
    }

    //TODO:: 게시판별 키워드를 레디스에 저장해서 조회하는것이 더 빠를듯
    private UserInfoInBoardResponse getUserInfoInBoardResponse(Board findBoardByBoardId) {
        Users findUserByBoard = userRepository.findById(findBoardByBoardId.getWriter().getUserId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return UserInfoInBoardResponse.builder()
                .nickname(findUserByBoard.getNickname())
                .profileImage(findUserByBoard.getProfileImage())
                .keyword(findUserByBoard.getBoardKeywords().stream().map(
                        boardKeywords-> boardKeywords.getBoardKeyword().getKoreanValue()
                ).toList())
                .simpleIntroduce(findUserByBoard.getSimpleIntroduce())
                .build();
    }

    private static List<String> getImageList(Board findBoardByBoardId) {
        return Optional.ofNullable(findBoardByBoardId.getBoardFiles())
                .orElse(Collections.emptyList())
                .stream()
                .map(BoardFiles::getBoardFileName)
                .toList();
    }

    private static List<String> getBoardKeywords(Board findBoardByBoardId) {
        // 키워드 리스트 생성 (비어있을 경우 빈 리스트 반환)
        return Optional.ofNullable(findBoardByBoardId.getBoardKeywordForCreatings())
                .orElse(Collections.emptyList())
                .stream()
                .map(keyword -> keyword.getBoardKeyword().getKoreanValue())
                .toList();
    }

    public List<FindSimilarBoardResponse> findBoardContainsKeywords(CustomUserDetail customUserDetail) {
        Users findUserByUserDetail = getUsers(customUserDetail);
        List<FindSimilarBoardResponse> responseList = new ArrayList<>();

        List<BoardKeywordEntity> boardKeywords = findUserByUserDetail.getBoardKeywords();
        if(boardKeywords == null || boardKeywords.isEmpty()) {
            List<Board> find3Boards = boardRepository.findTop3ByOrderByCreatedAtDesc().orElseThrow(
                    () -> new MemberException(ExceptionCode.NOT_FOUND_MORE_THAN_3_BOARDS)
            );
        }

        return null;
    }
}