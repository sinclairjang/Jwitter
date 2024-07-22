package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerobase.jwitter.domain.model.Follow;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.FollowRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;

    @org.zerobase.jwitter.domain.config.validation.Follow
    public void follow(Long followerId, Long followeeid) {
        Follow follow = new Follow(new User(followerId), new User(followeeid));
        followRepository.save(follow);
    }

    public Iterable<User> getFollowees(Long userId) {
        Set<Follow> followees = followRepository.findByfollower(new User(userId));
        return followees.stream().map(Follow::getFollowee).collect(Collectors.toSet());
    }

    public Iterable<User> getFollowers(Long userId) {
        Set<Follow> followers = followRepository.findByfollowee(new User(userId));
        return followers.stream().map(Follow::getFollower).collect(Collectors.toSet());
    }
}
