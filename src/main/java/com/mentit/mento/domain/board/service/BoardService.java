package com.mentit.mento.domain.board.service;

import com.mentit.mento.domain.board.domain.dto.request.BoardUpdate;
import com.mentit.mento.domain.board.domain.dto.request.CreateBoard;
import com.mentit.mento.domain.board.domain.dto.response.FindBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindMyBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindSimilarBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.UserInfoInBoardResponse;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardFilesEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.domain.entity.SavedBoardEntity;
import com.mentit.mento.domain.board.service.port.BoardFileRepository;
import com.mentit.mento.domain.board.service.port.BoardKeywordForCreatingRepository;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.board.service.port.SavedBoardRepository;
import com.mentit.mento.domain.comment.service.CommentService;
import com.mentit.mento.domain.comment.service.port.CommentRepository;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardKeywordForCreatingRepository boardKeywordForCreatingRepository;
    private final CommentRepository commentRepository;
    private final DotoriTokenRepository dotoriTokenRepository;
    private final CommentService commentService;
    private final S3FileUtilImpl s3FileUtilImpl;
    private final RedisLikeService redisLikeService;
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SavedBoardRepository savedBoardRepository;
    private final LocalContainerEntityManagerFactoryBean entityManager;

    @Transactional
    public FindBoardResponse createBoard(CustomUserDetail customUserDetail, CreateBoard createBoard, List<MultipartFile> images) {
        //유저 정보 조회
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        //게시판 작성자와 유저 닉네임이 일치하지 않으면 예외 발생
        if (!findUserByUserDetail.getNickname().equals(createBoard.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        //게시판 생성
        BoardEntity boardEntity = createBoard(createBoard, findUserByUserDetail);

        //게시판생성용키워드 생성
        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = getBoardKeywordForCreatingEntities(createBoard, boardEntity);

        List<BoardFilesEntity> boardFileEntities = getBoardFiles(images, boardEntity);

        BoardEntity createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(boardEntity, boardFileEntities, boardKeywordForCreatingEntityList);

        DotoriTokenEntity dotoriToken = findUserByUserDetail.getDotoriTokenEntity();
        DotoriTokenEntity updatedDotoriToken = dotoriToken
                .toBuilder()
                .count(createBoard
                        .getBoardType()
                        .getKoreanValue()
                        .equals("IT 일상") ? dotoriToken.getCount() : dotoriToken.getCount() + 5)
                .build();

        dotoriTokenRepository.save(updatedDotoriToken);

        redisService.saveBoardKeywords(boardEntity.getBoardId(), createBoard.getKeywords());

        return findOneBoard(createdBoard.getBoardId());

    }

    @Transactional
    public FindBoardResponse updateBoard(CustomUserDetail customUserDetail, BoardUpdate boardUpdate, List<MultipartFile> images) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(boardUpdate.getWriter())) {
            throw new MemberException(ExceptionCode.NICKNAME_NOT_MATCH);
        }

        BoardEntity boardEntity = boardRepository.findByBoardId(boardUpdate.getBoardId()).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );

        redisService.deleteBoardKeywords(boardEntity.getBoardId());
        boardKeywordForCreatingRepository.deleteAllByBoard(boardEntity);

        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingList = getBoardKeywordForCreatingEntities(boardUpdate, boardEntity);

        BoardEntity updatedBoard = updateBoard(boardUpdate, boardEntity);

        List<BoardFilesEntity> boardList = boardEntity.getBoardFileEntities();

        if (boardList != null && !boardList.isEmpty()) {
            boardFileRepository.deleteAllByBoardEntity(boardEntity);
        }

        List<BoardFilesEntity> boardFileEntities = getBoardFiles(images, updatedBoard);

        BoardEntity createdBoard = mappingBoardFileAndBoardKeywordInSavedBoard(boardEntity, boardFileEntities, boardKeywordForCreatingList);

        boardKeywordForCreatingRepository.flush();
        boardFileRepository.flush();

        return findOneBoard(createdBoard.getBoardId());
    }

    @Transactional
    public void deleteBoard(CustomUserDetail customUserDetail, Long boardId) {
        BoardEntity findBoard = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_BOARD)
        );

        List<BoardFilesEntity> boardFile = boardFileRepository.findAllByBoardEntity(findBoard);
        boardFile.forEach(
                files -> s3FileUtilImpl.deleteImageFromS3(files.getBoardFileName())
        );

        redisService.deleteBoardKeywords(findBoard.getBoardId());

        commentRepository.deleteAllByBoard(findBoard);

        boardFileRepository.deleteAllByBoardEntity(findBoard);

        boardKeywordForCreatingRepository.deleteAllByBoard(findBoard);

        boardRepository.delete(findBoard);

    }

    //게시판 단일 조회
    public FindBoardResponse findOneBoard( Long boardId) {
        BoardEntity findBoardByBoardId = boardRepository.findByBoardId(boardId).orElseThrow(
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
                .boardId(boardId)
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


    //조건 맞추기, 레디스에 키워드 저장하는 서비스 먼저 생성
    // 1. 해당 게시물과 겹치는 키워드 수가 많은 순서대로 상단 노출
    // 2. 겹치는 키워드 수가 같다면 최신순 상단 노출
    // 3. 겹치는 키워드가 없다면 해당 게시판 게시물 최신순 상단 노출
    //유사키워드 게시판 추출
    public List<FindSimilarBoardResponse> findBoardContainsKeywords(CustomUserDetail customUserDetail) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

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
                    BoardEntity boardEntity1 = boardRepository.findByBoardId(b1.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    BoardEntity boardEntity2 = boardRepository.findByBoardId(b2.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD));
                    return boardEntity2.getCreatedAt().compareTo(boardEntity1.getCreatedAt());
                })
                .limit(3)
                .toList();


        List<BoardEntity> matchedBoard = boardMatchCounts.stream().map(
                boardMatchCount -> boardRepository.findByBoardId(boardMatchCount.boardId).orElseThrow(() -> new BoardException(ExceptionCode.NOT_FOUND_BOARD))
        ).toList();

        return mapBoardsToResponse(matchedBoard);

    }

    //조회수순 3개 추출
    public List<FindBoardResponse> findTop3Boards(CustomUserDetail customUserDetail) {

        UsersEntity findUserByUserDetail = getUsers(customUserDetail);

        return boardRepository.findTop3ByOrderByViewCountDesc()
                .map(boards -> boards.stream().map(
                        board -> {

                            return FindBoardResponse.builder()
                                    .boardId(board.getBoardId())
                                    .title(board.getTitle())
                                    .content(board.getContent())
                                    .createdTime(board.getCreatedAt())
                                    .viewCount(board.getViewCount())
                                    .imageList(getImageList(board))
                                    .boardKeywords(getBoardKeywords(board))
                                    .userInfo(getUserInfoInBoardResponse(board))
                                    .likeCount(redisLikeService.getLikeCount(board.getBoardId()))
                                    .writer(findUserByUserDetail.getNickname())
                                    .commentCount(commentService.getCommentCount(board.getBoardId()))
                                    .build();

                        }
                ).toList()).orElse(Collections.emptyList());

    }
    //내가쓴게시판
    public Page<FindMyBoardResponse> findMyBoards(CustomUserDetail customUserDetail, Pageable pageable) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail);
        Page<BoardEntity> findBoards = boardRepository.findByUsers(findUserByUserDetail,pageable);

        //FindBoardResponse로 변환
        List<FindMyBoardResponse> findBoardResponseList = findBoards.map(
                boardEntity ->{
                   return FindMyBoardResponse.builder()
                            .boardId(boardEntity.getBoardId())
                           .writer(findUserByUserDetail.getNickname())
                            .title(boardEntity.getTitle())
                            .content(boardEntity.getContent())
                            .createdTime(boardEntity.getCreatedAt())
                            .boardKeywords(getBoardKeywords(boardEntity))
                            .viewCount(boardEntity.getViewCount())
                            .likeCount(redisLikeService.getLikeCount(boardEntity.getBoardId()))
                            .imageList(getImageList(boardEntity))
                            .build();
                }
        ).toList();

        return new PageImpl<>(findBoardResponseList, pageable, findBoards.getTotalElements());
    }

    //게시판 저장
    public void saveBoard(CustomUserDetail customUserDetail, Long boardId) {
        UsersEntity usersEntity = getUsers(customUserDetail);
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );

        SavedBoardEntity findSavedBoard = savedBoardRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
        if(findSavedBoard.getUserEntity().equals(usersEntity)) {
            throw new BoardException(ExceptionCode.ALREADY_SAVED_BAORD);
        }

        SavedBoardEntity savedBoardEntity = SavedBoardEntity.builder()
                .userEntity(usersEntity)
                .boardEntity(boardEntity)
                .build();

        savedBoardRepository.save(savedBoardEntity);

    }

    //게시판 저장 삭제
    @Transactional
    public void deleteSavedBoard(CustomUserDetail customUserDetail, Long boardId) {
        UsersEntity usersEntity = getUsers(customUserDetail);
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId).orElseThrow(
                () -> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
        savedBoardRepository.deleteByBoardAndUser(boardEntity,usersEntity);
        savedBoardRepository.flush();
    }

    public Page<FindBoardResponse> findMySavedBoards(CustomUserDetail customUserDetail, Pageable pageable) {
        UsersEntity usersEntity = getUsers(customUserDetail);
        List<SavedBoardEntity> savedBoardEntities = savedBoardRepository.findAllByUsers(usersEntity);
        List<BoardEntity> boardEntities = savedBoardEntities.stream().map(
                sbe -> sbe.getBoardEntity()
        ).toList();
        List<FindBoardResponse> findBoardResponses = boardEntities.stream().map(
                boardEntity ->
                        FindBoardResponse.builder()
                                .boardId(boardEntity.getBoardId())
                                .title(boardEntity.getTitle())
                                .writer(boardEntity.getWriter().getNickname())
                                .content(boardEntity.getContent())
                                .imageList(getImageList(boardEntity))
                                .likeCount(redisLikeService.getLikeCount(boardEntity.getBoardId()))
                                .viewCount(boardEntity.getViewCount())
                                .boardKeywords(getBoardKeywords(boardEntity))
                                .userInfo(getUserInfoInBoardResponse(boardEntity))
                                .commentCount(commentService.getCommentCount(boardEntity.getBoardId()))
                                .createdTime(boardEntity.getCreatedAt())
                                .build()
        ).toList();

        return new PageImpl<>(findBoardResponses, pageable, findBoardResponses.size());

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
    private List<FindSimilarBoardResponse> mapBoardsToResponse(List<BoardEntity> boardList) {
        return boardList.stream()
                .map(board -> {
                    List<String> boardKeywords = getBoardKeywords(board);
                    List<String> imageList = getImageList(board);
                    Long likeCount = redisLikeService.getLikeCount(board.getBoardId());
                    Long commentCount = commentService.getCommentCount(board.getBoardId());
                    UserInfoInBoardResponse userInfo = getUserInfoInBoardResponse(board);

                    return FindSimilarBoardResponse.builder()
                            .boardId(board.getBoardId())
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
                .toList();
    }
    //TODO:: 게시판별 키워드를 레디스에 저장해서 조회하는것이 더 빠를듯
    private UserInfoInBoardResponse getUserInfoInBoardResponse(BoardEntity findBoardByBoardId) {
        UsersEntity findUserByBoard = userRepository.findById(findBoardByBoardId.getWriter().getUserId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return UserInfoInBoardResponse.builder()
                .userId(findUserByBoard.getUserId())
                .nickname(findUserByBoard.getNickname())
                .profileImage(findUserByBoard.getProfileImage())
                .keyword(findUserByBoard.getBoardKeywords().stream().map(
                        boardKeywords -> boardKeywords.getBoardKeywordEnum().getKoreanValue()
                ).toList())
                .simpleIntroduce(findUserByBoard.getSimpleIntroduce())
                .build();
    }
    private static List<String> getImageList(BoardEntity findBoardByBoardId) {
        return Optional.ofNullable(findBoardByBoardId.getBoardFileEntities())
                .orElse(Collections.emptyList())
                .stream()
                .map(BoardFilesEntity::getBoardFileName)
                .toList();
    }

    private static List<String> getBoardKeywords(BoardEntity findBoardByBoardId) {
        // 키워드 리스트 생성 (비어있을 경우 빈 리스트 반환)
        return Optional.ofNullable(findBoardByBoardId.getBoardKeywordForCreatings())
                .orElse(Collections.emptyList())
                .stream()
                .map(keyword -> keyword.getBoardKeyword().getKoreanValue())
                .toList();
    }

    private BoardEntity mappingBoardFileAndBoardKeywordInSavedBoard(BoardEntity boardEntity, List<BoardFilesEntity> boardFilesEntities, List<BoardKeywordForCreatingEntity> boardKeywordForCreatingList) {
        BoardEntity updatedSavedBoard = boardEntity.toBuilder()
                .boardFileEntities(boardFilesEntities)
                .boardKeywordForCreatings(boardKeywordForCreatingList)
                .build();

        return boardRepository.save(updatedSavedBoard);
    }

    private BoardEntity createBoard(CreateBoard createBoard, UsersEntity usersEntity) {
        BoardEntity boardEntity = BoardEntity.builder()
                .title(createBoard.getTitle())
                .content(createBoard.getContent())
                .writer(usersEntity)
                .boardTypeEnum(createBoard.getBoardType())
                .viewCount(1L)
                .build();

        return boardRepository.save(boardEntity);
    }

    private BoardEntity updateBoard(BoardUpdate boardUpdate, BoardEntity curBoard) {
        BoardEntity createdBoard = curBoard.toBuilder()
                .title(boardUpdate.getTitle())
                .content(boardUpdate.getContent())
                .boardTypeEnum(boardUpdate.getBoardType())
                .viewCount(1L)
                .build();

        return boardRepository.save(createdBoard);
    }

    private List<BoardKeywordForCreatingEntity> getBoardKeywordForCreatingEntities(CreateBoard createBoard, BoardEntity boardEntity) {
        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingEntityList = new ArrayList<>();

        if (createBoard.getKeywords() != null) {

            createBoard.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreatingEntity savedBoardKeyword = BoardKeywordForCreatingEntity.builder()
                                .boardEntity(boardEntity)
                                .boardKeyword(keyword)
                                .build();

                        BoardKeywordForCreatingEntity savedBoardKeywordForCreating = boardKeywordForCreatingRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingEntityList.add(savedBoardKeywordForCreating);
                    }
            );
        }
        return boardKeywordForCreatingEntityList;
    }

    private List<BoardKeywordForCreatingEntity> getBoardKeywordForCreatingEntities(BoardUpdate boardUpdate, BoardEntity findBoard) {
        List<BoardKeywordForCreatingEntity> boardKeywordForCreatingList = new ArrayList<>();
        if (boardUpdate.getKeywords() != null) {

            boardUpdate.getKeywords().forEach(
                    keyword -> {
                        BoardKeywordForCreatingEntity savedBoardKeyword = BoardKeywordForCreatingEntity.builder()
                                .boardEntity(findBoard)
                                .boardKeyword(keyword)
                                .build();

                        BoardKeywordForCreatingEntity savedBoardKeywordForCreating = boardKeywordForCreatingRepository.save(savedBoardKeyword);
                        boardKeywordForCreatingList.add(savedBoardKeywordForCreating);
                    }
            );
        }
        return boardKeywordForCreatingList;
    }

    private List<BoardFilesEntity> getBoardFiles(List<MultipartFile> images, BoardEntity boardEntity) {
        List<BoardFilesEntity> boardFiles = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String uploadedUrl = s3FileUtilImpl.upload(image);
                BoardFilesEntity createdFile = BoardFilesEntity.builder()
                        .boardFileName(uploadedUrl)
                        .boardEntity(boardEntity)
                        .build();
                BoardFilesEntity savedBoardFile = boardFileRepository.save(createdFile);
                boardFiles.add(savedBoardFile);
            }
        }
        return boardFiles;
    }


    private UsersEntity getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}

