package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerobase.jwitter.api.aop.exception.JweetCommentNotFoundException;
import org.zerobase.jwitter.api.aop.exception.JweetNotFoundException;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.model.cache.HomeTimelineCache;
import org.zerobase.jwitter.domain.model.cache.JweetCache;
import org.zerobase.jwitter.domain.repository.JweetCommentRepository;
import org.zerobase.jwitter.domain.repository.JweetRepository;
import org.zerobase.jwitter.domain.repository.cache.HomeTimelineCacheRepository;
import org.zerobase.jwitter.domain.repository.cache.JweetCacheRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class JweetCRUDService {
    private final JweetRepository jweetRepository;
    private final HomeTimelineCacheRepository homeTimelineCacheRepository;
    private final JweetCacheRepository jweetCacheRepository;
    private final FollowService followService;
    private final JweetCommentRepository jweetCommentRepository;

    public Jweet readJweet(Long id) {
        return jweetRepository.findById(id).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", id)
                )
        );
    }

    public Jweet postJweet(Jweet jweet) {
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
        return persistentJweet;
    }

    @Transactional
    public Jweet editJweet(Long jweetId, String text) {
        Jweet persistentJweet = jweetRepository.findById(jweetId).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", jweetId)
                )
        );

        Iterable<User> followers = followService.getFollowers(persistentJweet.getAuthorId());
        if (!persistentJweet.getText().equals(text)) {
            persistentJweet.setText(text);
            followers.forEach(follower -> {
                homeTimelineCacheRepository.updateText(
                        new HomeTimelineCache(
                                String.valueOf(follower.getId()),
                                Set.of(JweetCache.from(persistentJweet))
                        )
                );
            });
        }

        jweetRepository.save(persistentJweet);
        return persistentJweet;
    }

    @Transactional
    public void deleteJweet(Long id) {
        Jweet persistentJweet = jweetRepository.findById(id).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", id)
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
    public Jweet likeJweet(Long id) {
        Jweet persistentJweet = jweetRepository.findById(id).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", id)
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
        return jweetRepository.save(persistentJweet);
    }

    public Page<JweetComment> readJweetComments(Long jweetId,
                                                Pageable page) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", jweetId)
                )
        );
        return jweetCommentRepository.findAll(page);
    }

    @Transactional
    public JweetComment postJweetComment(Long jweetId, JweetComment comment) {
        Jweet persistentJweet =
                jweetRepository.findById(jweetId).orElseThrow(
                        () -> new JweetNotFoundException(
                                String.format("Jweet:%d does not exist.", jweetId)
                        )
                );
        persistentJweet.addJweetComments(comment);
        jweetRepository.flush();
        return jweetCommentRepository.findById(comment.getId()).orElseThrow(
                () -> new JweetCommentNotFoundException(
                        String.format("Jweet comment:%d does not exist.", jweetId)
                )
        );
    }

    @Transactional
    public JweetComment editJweetComment(Long jweetId,
                                         Long commentId,
                                         String text) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", jweetId)
                )
        );
        JweetComment persistentComment =
                jweetCommentRepository.findById(commentId).orElseThrow(
                        () -> new JweetCommentNotFoundException(
                                String.format("Jweet comment:%d doesn't exist.", commentId)
                        )
                );
        persistentComment.setText(text);
        return persistentComment;
    }

    @Transactional
    public void deleteJweetComment(Long jweetId, Long commentId) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", jweetId)
                )
        );
        jweetCommentRepository.deleteById(commentId);
    }

    @Transactional
    public JweetComment likeJweetComment(Long jweetId, Long commentId) {
        jweetRepository.findById(jweetId).orElseThrow(
                () -> new JweetNotFoundException(
                        String.format("Jweet:%d does not exist.", jweetId)
                )
        );
        JweetComment persistentComment =
                jweetCommentRepository.findById(commentId).orElseThrow(
                        () -> new JweetCommentNotFoundException(
                                String.format("Jweet comment:%d doesn't exist.", commentId)
                        )
                );
        persistentComment.setLikes(persistentComment.getLikes() + 1);
        return persistentComment;
    }
}