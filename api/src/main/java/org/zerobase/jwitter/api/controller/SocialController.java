package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zerobase.jwitter.api.service.FollowService;
import org.zerobase.jwitter.domain.aop.validation.Follow;
import org.zerobase.jwitter.domain.model.User;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/social")
public class SocialController {
    private final FollowService followService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all_followees/{id}")
    public Iterable<User> getFollowees(@PathVariable Long id) {
        return followService.getFollowees(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followees/{id}")
    public Iterable<User> getFolloweesByPage(
            @PathVariable Long id,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return followService.getFollowees(id, PageRequest.of(page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all_followers/{id}")
    public Iterable<User> getFollowers(@PathVariable Long id) {
        return followService.getFollowers(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/followers/{id}")
    public Iterable<User> getFollowersByPage(
            @PathVariable Long id,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return followService.getFollowers(id, PageRequest.of(page, size));
    }

    @Follow
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #followerId, '')")
    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestParam(required = true) Long followerId,
                                    @RequestParam(required = true) Long followeeId) {

        followService.follow(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @Follow
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #followerId, '')")
    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow( @RequestParam(required = true) Long followerId,
                                       @RequestParam(required = true) Long followeeId) {

        followService.unfollow(followerId, followeeId);
        return ResponseEntity.ok().build();
    }
}
