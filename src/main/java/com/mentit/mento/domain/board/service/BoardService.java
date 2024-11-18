package com.mentit.mento.domain.board.service;

import com.mentit.mento.domain.board.dto.*;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.entity.BoardFiles;
import com.mentit.mento.domain.board.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.repository.BoardFileRepository;
import com.mentit.mento.domain.board.repository.BoardKeywordForCreatingEntityRepository;
import com.mentit.mento.domain.board.repository.BoardRepository;
import com.mentit.mento.domain.comment.repository.CommentRepository;
import com.mentit.mento.domain.comment.service.CommentService;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenRepository;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.UserRepositoryImpl;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.BoardException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.redis.service.RedisLikeService;
import com.mentit.mento.global.redis.service.RedisService;
import com.mentit.mento.global.s3.S3FileUtilImpl;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final UserRepositoryImpl userRepositoryImpl;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardKeywordForCreatingEntityRepository boardKeywordForCreatingEntityRepository;
    private final CommentRepository commentRepository;
    private final RedisLikeService redisLikeService;
    private final CommentService commentService;
    private final DotoriTokenRepository dotoriTokenRepository;
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public FindBoardResponse createBoard(CustomUserDetail customUserDetail, BoardCreateRequest boardCreateRequest, List<MultipartFile> images) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardCreateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        Board savedboard = createBoard(boardCreateRequest, findUserByUserDetail);

        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(boardCreateRequest, savedboard);

        List<BoardFiles> boardFiles = getBoardFiles(images, savedboard);

        Board createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(savedboard, boardFiles, boardKeywordForCreatingEntityList);

        DotoriToken dotoriToken = findUserByUserDetail.getDotoriToken();
        DotoriToken updatedDotoriToken = dotoriToken
                .toBuilder()
                .count(boardCreateRequest
                        .getBoardType()
                        .getKoreanValue()
                        .equals("IT 일상") ? dotoriToken.getCount() : dotoriToken.getCount() + 5)
                .build();

        dotoriTokenRepository.save(updatedDotoriToken);

        redisService.saveBoardKeywords(savedboard.getBoardId(), boardCreateRequest.getKeywords());

        return findBoard(customUserDetail, createdBoard.getBoardId());

    }

    @Transactional
    public FindBoardResponse updateBoard(CustomUserDetail customUserDetail, BoardUpdateRequest boardUpdateRequest, List<MultipartFile> images) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardUpdateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        Board findBoard = boardRepository.findByBoardId(boardUpdateRequest.getBoardId()).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );

        redisService.deleteBoardKeywords(findBoard.getBoardId());
        boardKeywordForCreatingEntityRepository.deleteAllByBoard(findBoard);

        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(boardUpdateRequest, findBoard);

        Board savedboard = updateBoard(boardUpdateRequest, findBoard);

        List<BoardFiles> boardList = findBoard.getBoardFiles();

        if (boardList != null && !boardList.isEmpty()) {
            boardFileRepository.deleteAllByBoard(findBoard);
        }

        List<BoardFiles> boardFiles = getBoardFiles(images, savedboard);

        Board createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(findBoard, boardFiles, boardKeywordForCreatingEntityList);

        return findBoard(customUserDetail, createdBoard.getBoardId());
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

        redisService.deleteBoardKeywords(findBoard.getBoardId());

        commentRepository.deleteAllByBoard(findBoard);

        boardFileRepository.deleteAllByBoard(findBoard);

        boardKeywordForCreatingEntityRepository.deleteAllByBoard(findBoard);

        boardRepository.delete(findBoard);

    }

    private Board mappingBoardFileAndBoardKeywordInSavedBoard(Board savedboard, List<BoardFiles> boardFiles, List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList) {
        Board updatedSavedBoard = savedboard.toBuilder()
                .boardFiles(boardFiles)
                .boardKeywordForCreatings(boardKeywordForCreatingEntityList)
                .build();

        return boardRepository.save(updatedSavedBoard);
    }

    private Board createBoard(BoardCreateRequest boardCreateRequest, UsersEntity findUserByUserDetail) {
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


    private UsersEntity getUsers(CustomUserDetail userDetail) {
        return userRepositoryImpl.findById(userDetail.getId()).orElseThrow(
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
        UsersEntity findUserByBoard = userRepositoryImpl.findById(findBoardByBoardId.getWriter().getUserId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return UserInfoInBoardResponse.builder()
                .nickname(findUserByBoard.getNickname())
                .profileImage(findUserByBoard.getProfileImage())
                .keyword(findUserByBoard.getBoardKeywords().stream().map(
                        boardKeywords -> boardKeywords.getBoardKeywordEnum().getKoreanValue()
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

    //조건 맞추기, 레디스에 키워드 저장하는 서비스 먼저 생성
    // 1. 해당 게시물과 겹치는 키워드 수가 많은 순서대로 상단 노출
    // 2. 겹치는 키워드 수가 같다면 최신순 상단 노출
    // 3. 겹치는 키워드가 없다면 해당 게시판 게시물 최신순 상단 노출
    public List<FindSimilarBoardResponse> findBoardContainsKeywords(CustomUserDetail customUserDetail) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        // 키워드별로 게시물 매핑을 위한 구조 준비
        List<String> keywords = findUserByUserDetail.getBoardKeywords().stream()
                .map(BoardKeywordEntity::getBoardKeywordEnum)
                .map(Enum::name)
                .toList();

        Set<String> allBoardKeys = redisTemplate.keys("boardKeywords:*");
        if (allBoardKeys == null && allBoardKeys.isEmpty()) {
            return Collections.emptyList();
        }

        //게시판 ID 별로 키워드 일치 개수 계산
        List<BoardMatchCount> boardMatchCounts = allBoardKeys.stream()
                .map(key -> {
                    Long boardId = Long.parseLong(key.split(":")[1]);
                    long matchCount = redisService.countMatchingKeywords(boardId, keywords);
                    return new BoardMatchCount(boardId, matchCount);
                })
                .sorted((b1, b2) -> {
                    if (b1.matchCount != b2.matchCount) {
                        return Long.compare(b2.matchCount, b1.matchCount);
                    }
                    Board board1 = boardRepository.findById(b1.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    Board board2 = boardRepository.findByBoardId(b2.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    return board2.getCreatedAt().compareTo(board1.getCreatedAt());
                })
                .limit(3)
                .collect(Collectors.toList());


        List<Board> matchedBoards = boardMatchCounts.stream().map(
                boardMatchCount -> boardRepository.findById(boardMatchCount.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD))
        ).collect(Collectors.toList());

        return mapBoardsToResponse(matchedBoards);

    }

    private List<FindSimilarBoardResponse> mapBoardsToResponse(List<Board> boards) {
        return boards.stream()
                .map(board -> {
                    List<String> boardKeywords = getBoardKeywords(board);
                    List<String> imageList = getImageList(board);
                    Long likeCount = redisLikeService.getLikeCount(board.getBoardId());
                    Long commentCount = commentService.getCommentCount(board.getBoardId());
                    UserInfoInBoardResponse userInfo = getUserInfoInBoardResponse(board);

                    return FindSimilarBoardResponse.builder()
                            .title(board.getTitle())
                            .writer(userInfo.getNickname())
                            .content(board.getContent())
                            .createdTime(board.getCreatedAt())
                            .boardKeywords(boardKeywords)
                            .userInfo(userInfo)
                            .imageList(!imageList.isEmpty() ? imageList.get(0) : null) // 이미지 리스트를 ','로 연결
                            .viewCount(board.getViewCount())
                            .likeCount(likeCount)
                            .commentCount(commentCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<FindBoardResponse> findTop3Boards(CustomUserDetail customUserDetail) {

        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        return boardRepository.findTop3ByOrderByViewCountDesc()
                .map(boards -> boards.stream().map(
                        board -> {
                            List<String> imageList = getImageList(board);

                            return FindBoardResponse.builder()
                                    .title(board.getTitle())
                                    .content(board.getContent())
                                    .createdTime(board.getCreatedAt())
                                    .viewCount(board.getViewCount())
                                    .imageList(imageList)
                                    .boardKeywords(getBoardKeywords(board))
                                    .userInfo(getUserInfoInBoardResponse(board))
                                    .imageList(imageList)
                                    .writer(findUserByUserDetail.getNickname())
                                    .commentCount(commentService.getCommentCount(board.getBoardId()))
                                    .build();

                        }
                ).collect(Collectors.toList())).orElse(Collections.emptyList());

    }

    // Board ID와 일치 개수를 담는 클래스
    private static class BoardMatchCount {
        Long boardId;
        long matchCount;

        public BoardMatchCount(Long boardId, long matchCount) {
            this.boardId = boardId;
            this.matchCount = matchCount;
        }
    }
}

