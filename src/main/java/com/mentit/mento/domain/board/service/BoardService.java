package com.mentit.mento.domain.board.service;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.BoardFiles;
import com.mentit.mento.domain.board.domain.BoardKeywordForCreating;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.domain.dto.request.BoardCreate;
import com.mentit.mento.domain.board.domain.dto.request.BoardUpdate;
import com.mentit.mento.domain.board.domain.dto.response.FindBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindSimilarBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.UserInfoInBoardResponse;
import com.mentit.mento.domain.board.service.port.BoardFileRepository;
import com.mentit.mento.domain.board.service.port.BoardKeywordForCreatingRepository;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.comment.repository.CommentJPARepository;
import com.mentit.mento.domain.comment.service.CommentService;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenRepository;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
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

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardKeywordForCreatingRepository boardKeywordForCreatingRepository;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final CommentJPARepository commentJPARepository;
    private final RedisLikeService redisLikeService;
    private final CommentService commentService;
    private final DotoriTokenRepository dotoriTokenRepository;
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public FindBoardResponse createBoard(CustomUserDetail customUserDetail, BoardCreate boardCreate, List<MultipartFile> images) {
        //유저 정보 조회
        Users findUserByUserDetail = getUsers(customUserDetail);

        //게시판 작성자와 유저 닉네임이 일치하지 않으면 예외 발생
        if (!findUserByUserDetail.getNickname().equals(boardCreate.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        //게시판 생성
        Board savedBoard = createBoard(boardCreate, findUserByUserDetail);

        //게시판생성용키워드 생성
        List<BoardKeywordForCreating> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(boardCreate, savedBoard);

        List<BoardFiles> boardFileEntities = getBoardFiles(images, savedBoard);

        Board createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(savedBoard, boardFileEntities, boardKeywordForCreatingEntityList);

        DotoriToken dotoriToken = findUserByUserDetail.getDotoriToken();
        DotoriToken updatedDotoriToken = dotoriToken
                .toBuilder()
                .count(boardCreate
                        .getBoardTypeEnum()
                        .getKoreanValue()
                        .equals("IT 일상") ? dotoriToken.getCount() : dotoriToken.getCount() + 5)
                .build();

        dotoriTokenRepository.save(updatedDotoriToken);

        redisService.saveBoardKeywords(savedBoard.getBoardId(), boardCreate.getKeywords());

        return findBoard(customUserDetail, createdBoard.getBoardId());

    }

    @Transactional
    public FindBoardResponse updateBoard(CustomUserDetail customUserDetail, BoardUpdate boardUpdate, List<MultipartFile> images) {
        Users findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardUpdate.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        Board findBoard = boardRepository.findByBoardId(boardUpdate.getBoardId()).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );

        redisService.deleteBoardKeywords(findBoard.getBoardId());
        boardKeywordForCreatingRepository.deleteAllByBoard(findBoard);

        List<BoardKeywordForCreating> boardKeywordForCreatingList = getBoardKeywordForCreatingEntities(boardUpdate, findBoard);

        Board updatedBoard = updateBoard(boardUpdate, findBoard);

        List<BoardFiles> boardList = findBoard.getBoardFiles();

        if (boardList != null && !boardList.isEmpty()) {
            boardFileRepository.deleteAllByBoard(findBoard);
        }

        List<BoardFiles> boardFileEntities = getBoardFiles(images, updatedBoard);

        Board createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(findBoard, boardFileEntities, boardKeywordForCreatingList);

        return findBoard(customUserDetail, createdBoard.getBoardId());
    }

    @Transactional
    public void deleteBoard(CustomUserDetail customUserDetail, Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_BOARD)
        );

        List<BoardFiles> boardFile = boardFileRepository.findAllByBoard(findBoard);
        boardFile.forEach(
                files -> {
                    s3FileUtilImpl.deleteImageFromS3(files.getBoardFileName());
                }
        );

        redisService.deleteBoardKeywords(findBoard.getBoardId());

        commentJPARepository.deleteAllByBoard(BoardEntity.from(findBoard));

        boardFileRepository.deleteAllByBoard(findBoard);

        boardKeywordForCreatingRepository.deleteAllByBoard(findBoard);

        boardRepository.delete(findBoard);

    }

    private Board mappingBoardFileAndBoardKeywordInSavedBoard(Board savedboard, List<BoardFiles> boardFiles, List<BoardKeywordForCreating> boardKeywordForCreatingList) {
        Board updatedSavedBoard = savedboard.toBuilder()
                .boardFiles(boardFiles)
                .boardKeywordForCreatings(boardKeywordForCreatingList)
                .build();

        return boardRepository.save(updatedSavedBoard);
    }

    private Board createBoard(BoardCreate boardCreate, Users findUserByUserDetail) {
        Board createdBoard = Board.builder()
                .title(boardCreate.getTitle())
                .content(boardCreate.getContent())
                .writer(UsersEntity.from(findUserByUserDetail))
                .boardTypeEnum(boardCreate.getBoardTypeEnum())
                .viewCount(1L)
                .build();

        return boardRepository.save(createdBoard);
    }

    private Board updateBoard(BoardUpdate boardUpdate, Board curBoard) {
        Board createdBoard = curBoard.toBuilder()
                .title(boardUpdate.getTitle())
                .content(boardUpdate.getContent())
                .boardTypeEnum(boardUpdate.getBoardTypeEnum())
                .viewCount(1L)
                .build();

        return boardRepository.save(createdBoard);
    }

    private List<BoardKeywordForCreating> getBoardKeywordForCreatingEntities(BoardCreate boardCreate, Board board) {
        List<BoardKeywordForCreating> boardKeywordForCreatingEntityList = new ArrayList<>();

        if (boardCreate.getKeywords() != null) {

            boardCreate.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreating savedBoardKeyword = BoardKeywordForCreating.builder()
                                .boardEntity(board)
                                .boardKeyword(keyword)
                                .build();

                        BoardKeywordForCreating savedBoardKeywordForCreating = boardKeywordForCreatingRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingEntityList.add(savedBoardKeywordForCreating);
                    }
            );
        }
        return boardKeywordForCreatingEntityList;
    }

    private List<BoardKeywordForCreating> getBoardKeywordForCreatingEntities(BoardUpdate boardUpdate, Board findBoard) {
        List<BoardKeywordForCreating> boardKeywordForCreatingList = new ArrayList<>();
        if (boardUpdate.getKeywords() != null) {

            boardUpdate.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreating savedBoardKeyword = BoardKeywordForCreatingEntity.builder()
                                .boardEntity(BoardEntity.from(findBoard))
                                .boardKeyword(keyword)
                                .build().to();

                        BoardKeywordForCreating savedBoardKeywordForCreating = boardKeywordForCreatingRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingList.add(savedBoardKeywordForCreating);
                    }
            );
        }
        return boardKeywordForCreatingList;
    }


    private List<BoardFiles> getBoardFiles(List<MultipartFile> images, Board savedboard) {
        List<BoardFiles> boardFiles = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String uploadedUrl = s3FileUtilImpl.upload(image);
                BoardFiles createdFile = BoardFiles.builder()
                        .boardFileName(uploadedUrl)
                        .boardEntity(savedboard)
                        .build();
                BoardFiles savedBoardFile = boardFileRepository.save(createdFile);
                boardFiles.add(savedBoardFile);
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
        Users findUserByUserDetail = getUsers(customUserDetail);

        // 키워드별로 게시물 매핑을 위한 구조 준비
        List<String> keywords = findUserByUserDetail.getBoardKeywords().stream()
                .map(BoardKeywordEntity::getBoardKeywordEnum)
                .map(Enum::name)
                .toList();

        Set<String> allBoardKeys = redisTemplate.keys("boardKeywords:*");
        if (allBoardKeys == null || allBoardKeys.isEmpty()) {
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
                    Board boardEntity1 = boardRepository.findById(b1.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    Board boardEntity2 = boardRepository.findByBoardId(b2.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    return boardEntity2.getCreatedAt().compareTo(boardEntity1.getCreatedAt());
                })
                .limit(3)
                .collect(Collectors.toList());


        List<Board> matchedBoard = boardMatchCounts.stream().map(
                boardMatchCount -> boardRepository.findById(boardMatchCount.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD))
        ).collect(Collectors.toList());

        return mapBoardsToResponse(matchedBoard);

    }

    private List<FindSimilarBoardResponse> mapBoardsToResponse(List<Board> boardList) {
        return boardList.stream()
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

        Users findUserByUserDetail = getUsers(customUserDetail);

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

