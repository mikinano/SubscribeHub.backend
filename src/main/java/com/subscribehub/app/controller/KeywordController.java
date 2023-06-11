package com.subscribehub.app.controller;

import com.subscribehub.app.service.KeywordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@AllArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;

    @GetMapping
    public List<String> getKeywords(Principal principal) {
        return keywordService.getKeywords(principal.getName());
    }

    @PutMapping
    public ResponseEntity<String> putKeywords(@RequestBody List<String> keywords, Principal principal) {
        keywordService.putKeywords(keywords, principal.getName());
        return ResponseEntity.ok("키워드 등록 성공");
    }
}