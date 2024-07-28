package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zerobase.jwitter.api.service.JweetCRUDService;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;

import javax.validation.Valid;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/jweet")
public class JweetController {
    private final JweetCRUDService jweetCRUDService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> readJweet(@PathVariable Long id) {
        return ResponseEntity.ok(jweetCRUDService.readJweet(id));
    }

    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweet, '')")
    @PostMapping()
    public ResponseEntity<?> postJweet(@RequestBody @Valid Jweet jweet) {
        jweetCRUDService.postJweet(jweet);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweetId, 'Jweet', '')")
    @PutMapping("/{jweetId}")
    public ResponseEntity<?> editJweet(
            @PathVariable Long jweetId,
            @RequestBody @Valid Jweet jweet) {
        jweetCRUDService.editJweet(jweetId, jweet);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #id, 'Jweet', '')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJweet(@PathVariable Long id) {
        jweetCRUDService.deleteJweet(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("{id}/like")
    public ResponseEntity<?> likeJweet(@PathVariable Long id) {
        jweetCRUDService.likeJweet(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{jweetId}/comment")
    public Page<JweetComment> readJweetComment(
            @PathVariable Long jweetId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return jweetCRUDService.readJweetComment(jweetId, PageRequest.of(page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{id}/comment")
    public ResponseEntity<?> postJweetComment(
            @PathVariable Long id,
            @RequestBody @Valid JweetComment jweetComment) {
        jweetCRUDService.postJweetComment(id, jweetComment);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetCommentPermission.hasPermission(authentication, #jweetComment, '')")
    @PutMapping("/{jweetId}/comment/{commentId}")
    public ResponseEntity<?> editJweetComment(
            @PathVariable Long jweetId,
            @PathVariable Long commentId,
            @RequestBody JweetComment jweetComment
    ) {
        jweetCRUDService.editJweetComment(jweetId, commentId, jweetComment);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetCommentPermission.hasPermission(authentication, #commentId, 'JweetComment', '')")
    @DeleteMapping("/{jweetId}/comment/{commentId}")
    public ResponseEntity<?> deleteJweetComment(
            @PathVariable Long jweetId,
            @PathVariable  Long commentId) {
        jweetCRUDService.deleteJweetComment(jweetId, commentId);
        return ResponseEntity.ok().build();
    }
}
