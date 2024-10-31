package com.mentit.mento.domain.users.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/intermediate")
    public ResponseEntity<Map<String, Object>> redirectToFrontend(
            @RequestParam String accessToken,
            @RequestParam boolean isNewUser) {

        Map<String, Object> response = new HashMap<>();

        response.put("accessToken", accessToken);
        response.put("isNewUser", isNewUser);

        return ResponseEntity.ok(response);
    }
}