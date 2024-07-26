package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.model.cache.HomeTimelineCache;
import org.zerobase.jwitter.domain.model.cache.JweetCache;
import org.zerobase.jwitter.domain.repository.JweetCommentRepository;
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
    private final JweetCommentRepository jweetCommentRepository;

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
    public void editJweet(Long jweetId, Jweet jweet) {
        Jweet persistentJweet = jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.",
                                jweet.getId())
                )
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
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.", id)
                )
        );
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
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.", id)
                )
        );
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

    public Optional<JweetComment> readJweetComment(Long jweetId,
                                                   Long commentId) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.", jweetId)
                )
        );
        return jweetCommentRepository.findById(commentId);
    }

    @Transactional
    public void postJweetComment(Long jweetId, JweetComment comment) {
        Jweet persistentJweet =
                jweetRepository.findById(jweetId).orElseThrow(
                        () -> new RuntimeException(
                                String.format("Jweet %d doesn't exist.", jweetId)
                        )
                );
        persistentJweet.addJweetComments(comment);
    }

    @Transactional
    public void editJweetComment(Long jweetId,
                                 Long commentId,
                                 JweetComment jweetComment) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.", jweetId)
                )
        );
       JweetComment persistentComment =
               jweetCommentRepository.findById(commentId).orElseThrow(
               () -> new RuntimeException(
                       String.format("Jweet comment %d doesn't exist.",
                               commentId)
               )
       );
        persistentComment.setText(jweetComment.getText());
    }

    @Transactional
    public void deleteJweetComment(Long jweetId, Long commentId) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet %d doesn't exist.", jweetId)
                )
        );
        jweetCommentRepository.deleteById(commentId);
    }
}
