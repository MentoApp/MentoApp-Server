package com.mentit.mento.domain.dotoriToken.controller;

import com.mentit.mento.domain.dotoriToken.dto.TokenGiftRequest;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriUsageResponse;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenService;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenUsageService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dotori-token")
@RequiredArgsConstructor
public class DotoriTokenController {

    private final DotoriTokenService dotoriTokenService;
    private final DotoriTokenUsageService dotoriTokenUsageService;


    @Operation(summary = "도토리 내역 조회" , description = "도토리 거래 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 조회 결과",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "정보 조회 실패",
                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
    })
    @GetMapping("/dotori-usage")
    public Response<Page<DotoriUsageResponse>> find(
            @Parameter(description = "페이지 시작 번호(0부터 시작)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 사이즈(3부터 시작)")
            @RequestParam(name = "size", defaultValue = "5") int size,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
            ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<DotoriUsageResponse> dotoriUsageList = dotoriTokenService.findUsage(pageable, customUserDetail);

        return Response.success(HttpStatus.OK,"도토리 사용 내역 조회 성공", dotoriUsageList);
    }

    @Operation(summary = "도토리 선물하기" , description = "도토리 선물 후 준사람과 받은 사람 개수를 업데이트함.")
    @PostMapping("/token-gift/board")
    public Response<Void> presentToken(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestBody TokenGiftRequest tokenGiftRequest
    ){
        DotoriTokenEntity dotoriTokenEntity = dotoriTokenService.updateDotoriToken(customUserDetail, tokenGiftRequest);
        dotoriTokenUsageService.create(customUserDetail, tokenGiftRequest,dotoriTokenEntity);

        return Response.success(HttpStatus.OK,"토큰 선물 완료");
    }

    @Operation(summary = "도토리 개수 조회" , description = "도토리 잔여 개수 조회")
    @GetMapping("/get-count")
    public Response<Integer> presentToken(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        int tokenCount = dotoriTokenService.getTokenCount(customUserDetail);

        return Response.success(HttpStatus.OK,"잔여 개수 조회 성공",tokenCount);
    }

}
