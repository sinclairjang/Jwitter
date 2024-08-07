package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerobase.jwitter.api.aop.exception.AlreadyFollowingException;
import org.zerobase.jwitter.api.aop.exception.AlreadyNotFollowingException;
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
    public Follow follow(Long followerId, Long followeeId) {
        followRepository.findByIds(followerId, followeeId).ifPresent(
                (follow) -> {
                    throw new AlreadyFollowingException(
                            String.format("User:%d is already following User:%d",
                                    followerId, followeeId)
                    );
                }
        );
        Follow follow = new Follow(new User(followerId), new User(followeeId));
        return followRepository.save(follow);
    }

    public Set<User> getFollowees(Long userId) {
        Set<Follow> followees = followRepository.findByfollower(new User(userId));
        return followees.stream().map(Follow::getFollowee).collect(Collectors.toSet());
    }

    public Page<Follow> getFollowees(Long userId, PageRequest page) {
        return followRepository.findByfollower(new User(userId), page);
    }

    public Set<User> getFollowers(Long userId) {
        Set<Follow> followers = followRepository.findByfollowee(new User(userId));
        return followers.stream().map(Follow::getFollower).collect(Collectors.toSet());
    }

    public Page<Follow> getFollowers(Long userId, Pageable page) {
        return followRepository.findByfollowee(new User(userId), page);
    }

    public void unfollow(Long followerId, Long followeeId) {
        if (followRepository.findByIds(followerId, followeeId).isEmpty()) {
            throw new AlreadyNotFollowingException(
                    String.format("User:%d is already not following User:%d",
                            followerId, followeeId)
            );
        }
        Follow follow = new Follow(new User(followerId), new User(followeeId));
        followRepository.delete(follow);
    }
}
