package com.mentit.mento.domain.dotoriToken.controller;

import com.mentit.mento.domain.dotoriToken.dto.request.DotoriUsageResponse;
import com.mentit.mento.domain.dotoriToken.service.DotoriTokenService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dotoriToken")
public class DotoriTokenController {

    private final DotoriTokenService dotoriTokenService;

    public DotoriTokenController(DotoriTokenService dotoriTokenService) {
        this.dotoriTokenService = dotoriTokenService;
    }

//    @Operation(summary = "도토리 내역 조회" , description = "도토리 거래 내역을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "닉네임 조회 결과",
//                    content = {@Content(schema = @Schema(implementation = Response.class))}),
//            @ApiResponse(responseCode = "400", description = "정보 조회 실패",
//                    content = {@Content(schema = @Schema(implementation = Exception.class))}),
//    })
//    @GetMapping("/random")
//    public ResponseEntity<Page<DotoriUsageResponse>> findRandom(
//            @Parameter(description = "페이지 시작 번호(0부터 시작)")
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @Parameter(description = "페이지 사이즈(5부터 시작)")
//            @RequestParam(name = "size", defaultValue = "5") int size,
//            @AuthenticationPrincipal CustomUserDetail customUserDetail
//            ) {
//        Pageable pageable = PageRequest.of(page, size);
//
////        Page<DotoriUsageResponse> dotoriUsageList = dotoriTokenService.findUsage(pageable, customUserDetail);
//
//        return ResponseEntity.ok().body(dotoriUsageList);
//    }

}
