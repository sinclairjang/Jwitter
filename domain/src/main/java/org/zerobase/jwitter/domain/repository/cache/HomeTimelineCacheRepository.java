package org.zerobase.jwitter.domain.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.repository.Repository;
import org.zerobase.jwitter.domain.exception.RedisInvalidCommandException;
import org.zerobase.jwitter.domain.exception.RedisKeyNotExistException;
import org.zerobase.jwitter.domain.model.cache.HomeTimelineCache;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.zerobase.jwitter.domain.model.cache.HomeTimelineCache.prefix;

@RequiredArgsConstructor
@org.springframework.stereotype.Repository
public class HomeTimelineCacheRepository implements Repository<HomeTimelineCache, String> {
    private final RedisTemplate<String, String> template;
    private final JweetCacheRepository jweetCacheRepository;

    @Value("${spring.redis.score}")
    private Double weight;

    private ZSetOperations<String, String> redisZSets() {
        return template.opsForZSet();
    }

    public <S extends HomeTimelineCache> void save(@NotNull S homeTimeline) {
        homeTimeline.getJweets().forEach(
                jweet -> {
                    jweetCacheRepository.save(jweet);
                    try {
                        if (Objects.requireNonNull(
                                redisZSets().zCard(homeTimeline.getId())).compareTo(800L) >= 0) {
                            redisZSets().removeRange(homeTimeline.getId(), 0, 1);
                        }
                    } catch (NullPointerException e) {
                        throw new RuntimeException(
                                "NullPointerException occured. Possibly due to transaction/pipeline"
                        );
                    }
                    redisZSets().add(homeTimeline.getId(),
                            jweet.getId(), Double.parseDouble(jweet.getCreatedAt()));
                }
        );
    }

    public <S extends HomeTimelineCache> void updateText(@NotNull S homeTimeline) {
        homeTimeline.getJweets().forEach(jweetCacheRepository::updateText);
    }

    public <S extends HomeTimelineCache> void updateLikes(@NotNull S homeTimeline) {
        homeTimeline.getJweets().forEach(
                jweet -> {
                    jweetCacheRepository.updateLikes(jweet);

                    try {
                        Objects.requireNonNull(
                                redisZSets().add(homeTimeline.getId(), jweet.getId(),
                                        Double.parseDouble(jweet.getCreatedAt()) +
                                                (weight * Double.parseDouble(jweet.getLikes()))));
                    } catch (NullPointerException e) {
                        throw new RuntimeException(
                                "NullPointerException occured. Possibly due to transaction/pipeline"
                        );
                    }
                }
        );
    }

    public Page<JweetCache> getHomeTimeline(@NotNull String id,
                                            @NotNull Pageable page) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        if (!existsById(id))
            throw new RedisKeyNotExistException(
                    String.format("%s doesn't exist.", id)
            );

        Set<String> jweetIds = redisZSets().reverseRange(
                id, page.getOffset(), page.getPageSize());

        try {
            if (Objects.requireNonNull(jweetIds).isEmpty()) {
                return new PageImpl<>(List.of());
            }
        } catch (NullPointerException e) {
            throw new RedisInvalidCommandException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }

        Set<JweetCache> jweets = new HashSet<>();
        jweetCacheRepository.findAllById(jweetIds).forEach(jweet -> {
            jweet.ifPresent(jweets::add);
        });
        return new PageImpl<>(jweets.stream().toList(), page, size(id));
    }

    public boolean existsById(@NotNull String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        try {
            if (!Objects.requireNonNull(template.hasKey(id))) {
                return false;
            }
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }

        return true;
    }

    public Long size(@NotNull String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        if (!existsById(id))
            throw new RuntimeException(
                    String.format("%s doesn't exist.", id)
            );

        try {
            Long size = redisZSets().zCard(id);
            Objects.requireNonNull(size);
            return size;
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
    }
    public long count() {
        try {
            Set<String> keys = template.keys(prefix + "*");
            Objects.requireNonNull(keys);
            return keys.size();
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
    }

    public void deleteById(@NotNull String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        try {
            Long deleted = redisZSets().removeRange(id, 0, -1);
            Objects.requireNonNull(deleted);
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
    }

    public void deleteAllById(@NotNull Iterable<? extends String> strings) {
        try {
            Collection<String> keys = new HashSet<>();
            strings.forEach(keys::add);
            Long deleted = template.delete(keys);
            Objects.requireNonNull(deleted);
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
    }

    public void deleteAll() {
        try {
            Set<String> keys = template.keys(prefix + "*");
            Objects.requireNonNull(keys);
            Long deleted = template.delete(keys);
            Objects.requireNonNull(deleted);
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
    }
}
