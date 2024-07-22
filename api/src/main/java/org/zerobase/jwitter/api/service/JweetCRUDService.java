package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.model.cache.HomeTimelineCache;
import org.zerobase.jwitter.domain.model.cache.JweetCache;
import org.zerobase.jwitter.domain.repository.FollowRepository;
import org.zerobase.jwitter.domain.repository.JweetRepository;
import org.zerobase.jwitter.domain.repository.cache.HomeTimelineCacheRepository;
import org.zerobase.jwitter.domain.repository.cache.JweetCacheRepository;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JweetCRUDService {
    private final JweetRepository jweetRepository;
    private final HomeTimelineCacheRepository homeTimelineCacheRepository;
    private final JweetCacheRepository jweetCacheRepository;
    private final FollowService followService;

    public Optional<Jweet> readJweet(Long id) {
        return jweetRepository.findById(id);
    }

    @Transactional
    public void postJweet(Jweet jweet) {
        Jweet persistentJweet = jweetRepository.save(jweet);
        Iterable<User> followers = followService.getFollowers(persistentJweet.getAuthorId());
        followers.forEach(follower -> {
            homeTimelineCacheRepository.save(
                    new HomeTimelineCache(
                            String.valueOf(follower.getId()),
                            Set.of(JweetCache.from(persistentJweet))
                    )
            );
        });
    }

    @Transactional
    public void modifyJweet(Jweet jweet) {
        Jweet persistentJweet = jweetRepository.findById(jweet.getId()).orElseThrow(
                () -> new RuntimeException("Modify-requested Jweet doesn't exist. ")
        );
        Iterable<User> followers = followService.getFollowers(persistentJweet.getAuthorId());
        if (!persistentJweet.getText().equals(jweet.getText())) {
            persistentJweet.setText(jweet.getText());
            persistentJweet.setLikes(jweet.getLikes());
            followers.forEach(follower -> {
                homeTimelineCacheRepository.updateText(
                        new HomeTimelineCache(
                                String.valueOf(follower.getId()),
                                Set.of(JweetCache.from(persistentJweet))
                        )
                );
            });
        }
        if (!persistentJweet.getLikes().equals(jweet.getLikes())) {
            persistentJweet.setLikes(jweet.getLikes());
            followers.forEach(follower -> {
                homeTimelineCacheRepository.updateLikes(
                        new HomeTimelineCache(
                                String.valueOf(follower.getId()),
                                Set.of(JweetCache.from(persistentJweet))
                        )
                );
            });
        }
        jweetRepository.save(persistentJweet);
    }

    @Transactional
    public void deleteJweet(Long id) {
        Jweet persistentJweet = jweetRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Modify-requested Jweet doesn't exist."));
        Iterable<User> followers = followService.getFollowers(persistentJweet.getAuthorId());
        followers.forEach(follower -> {
            homeTimelineCacheRepository.deleteById(String.valueOf(follower.getId()));
            jweetCacheRepository.deleteById(String.valueOf(persistentJweet.getId()));
        });
        jweetRepository.deleteById(id);
    }

    @Transactional
    public void likeJweet(Long id) {
        Jweet persistentJweet = jweetRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Modify-requested Jweet doesn't exist."));
        persistentJweet.setLikes(persistentJweet.getLikes() + 1);
        Iterable<User> followers = followService.getFollowers(persistentJweet.getAuthorId());
        followers.forEach(follower -> {
            homeTimelineCacheRepository.updateLikes(
                    new HomeTimelineCache(
                            String.valueOf(follower.getId()),
                            Set.of(JweetCache.from(persistentJweet))
                    )
            );
        });
        jweetRepository.save(persistentJweet);
    }

    @Transactional
    public void commentJweet(Long jweetId, JweetComment comment) {
        Jweet persistentJweet =
                jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException("Modify-requested Jweet doesn't exist."));
        persistentJweet.addJweetComments(comment);
    }

    @Transactional
    public void deleteCommentJweet(Long jweetId, Long commentId) {
        Jweet persistentJweet =
                jweetRepository.findById(jweetId).orElseThrow(
                        () -> new RuntimeException("Modify-requested Jweet doesn't exist."));
        persistentJweet.getJweetComments().remove(JweetComment.builder().id(commentId).build());
    }
}
