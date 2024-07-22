package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zerobase.jwitter.api.service.FollowService;
import org.zerobase.jwitter.domain.config.validation.Follow;
import org.zerobase.jwitter.domain.model.User;

import javax.validation.Valid;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/social")
public class SocialController {
    private final FollowService followService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followees/{id}")
    public Iterable<User> getFollowees(@PathVariable Long id) {
        return followService.getFollowees(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followers/{id}")
    public Iterable<User> getFollowers(@PathVariable Long id) {
        return followService.getFollowers(id);
    }

    @Follow
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #followerId, '')")
    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestParam(required = true) Long followerId,
                                    @RequestParam(required = true) Long followeeId) {

        followService.follow(followerId, followeeId);
        return ResponseEntity.ok().build();
    }
}
