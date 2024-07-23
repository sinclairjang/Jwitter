package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zerobase.jwitter.api.service.JweetCRUDService;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/jweet")
public class JweetController {
    private final JweetCRUDService jweetCRUDService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> readJweet(@PathVariable Long id) {
        Optional<Jweet> jweet = jweetCRUDService.readJweet(id);
        return ResponseEntity.of(jweet);
    }

    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweet, '')")
    @PostMapping()
    public ResponseEntity<?> postJweet(@RequestBody @Valid Jweet jweet) {
        jweetCRUDService.postJweet(jweet);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweet, '')")
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyJweet(
            @PathVariable Long jweetId,
            @RequestBody @Valid Jweet jweet) {
        jweetCRUDService.modifyJweet(jweetId, jweet);
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
    @GetMapping("/{jweetId}/comment/{commentId}")
    public ResponseEntity<?> readJweetComment(
            @PathVariable Long jweetId,
            @PathVariable Long commentId
    ) {
        Optional<JweetComment> jweetComment =
                jweetCRUDService.readJweetComment(jweetId, commentId);
        return ResponseEntity.ok(jweetComment);
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
    public ResponseEntity<?> modifyJweetComment(
            @PathVariable Long jweetId,
            @PathVariable Long commentId,
            @RequestBody JweetComment jweetComment
    ) {
        jweetCRUDService.modifyJweetComment(jweetId, commentId, jweetComment);
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
