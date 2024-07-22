package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
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
    @PutMapping()
    public ResponseEntity<?> modifyJweet(@RequestBody @Valid Jweet jweet) {
        jweetCRUDService.modifyJweet(jweet);
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
    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentJweet(
            @PathVariable Long id,
            @RequestBody @Valid JweetComment jweetComment) {
        jweetCRUDService.commentJweet(id, jweetComment);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@writeJweetCommentPermission.hasPermission(authentication, #commentId, 'JweetComment', '')")
    @DeleteMapping("/{jweetId}/comment/{commentId}")
    public ResponseEntity<?> deleteCommentJweet(
            @PathVariable Long jweetId,
            @PathVariable  Long commentId) {
        jweetCRUDService.deleteCommentJweet(jweetId, commentId);
        return ResponseEntity.ok().build();
    }
}
