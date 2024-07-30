package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @org.zerobase.jwitter.domain.aop.validation.Follow
    public void follow(Long followerId, Long followeeId) {
        Follow follow = new Follow(new User(followerId), new User(followeeId));
        followRepository.save(follow);
    }

    public Iterable<User> getFollowees(Long userId) {
        Set<Follow> followees = followRepository.findByfollower(new User(userId));
        return followees.stream().map(Follow::getFollowee).collect(Collectors.toSet());
    }

    public Iterable<User> getFollowees(Long userId, PageRequest page) {
        Page<Follow> followees = followRepository.findByfollower(new User(userId), page);
        return followees.stream().map(Follow::getFollowee).collect(Collectors.toSet());
    }

    public Iterable<User> getFollowers(Long userId) {
        Set<Follow> followers = followRepository.findByfollowee(new User(userId));
        return followers.stream().map(Follow::getFollower).collect(Collectors.toSet());
    }

    public Iterable<User> getFollowers(Long userId, Pageable page) {
        Page<Follow> followers = followRepository.findByfollowee(new User(userId), page);
        return followers.stream().map(Follow::getFollower).collect(Collectors.toSet());
    }

    public void unfollow(Long followerId, Long followeeId) {
        Follow follow = new Follow(new User(followerId), new User(followeeId));
        followRepository.delete(follow);
    }
}
