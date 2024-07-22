package org.zerobase.jwitter.domain.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zerobase.jwitter.domain.model.cache.HomeTimelineCache;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

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

    @Transactional
    public <S extends HomeTimelineCache> void save(@NonNull S homeTimeline) {
        homeTimeline.getJweets().forEach(
                jweet -> {
                    jweetCacheRepository.save(jweet);

                    try {
                        if (Objects.requireNonNull(
                                redisZSets().zCard(homeTimeline.getId())).compareTo(800L) > 0) {
                            throw new RuntimeException(
                                    "Home timeline capacity 800 has exceeded.");
                        }
                    } catch (NullPointerException e) {
                        throw new RuntimeException(
                                "NullPointerException occured. Possibly due to transaction/pipeline"
                        );
                    }

                    redisZSets().addIfAbsent(homeTimeline.getId(),
                            jweet.getId(), Double.parseDouble(jweet.getCreatedAt()));
                }
        );
    }

    @Transactional
    public <S extends HomeTimelineCache> void updateText(@NonNull S homeTimeline) {
        homeTimeline.getJweets().forEach(jweetCacheRepository::updateText);
    }

    @Transactional
    public <S extends HomeTimelineCache> void updateLikes(@NonNull S homeTimeline) {
        homeTimeline.getJweets().forEach(
                jweet -> {
                    jweetCacheRepository.updateLikes(jweet);

                    try {
                        Objects.requireNonNull(
                                redisZSets().add(homeTimeline.getId(), jweet.getId(),
                                        Double.parseDouble(jweet.getCreatedAt())
                                                + (weight * Double.parseDouble(jweet.getLikes()))));
                    } catch (NullPointerException e) {
                        throw new RuntimeException(
                                "NullPointerException occured. Possibly due to transaction/pipeline"
                        );
                    }
                }
        );
    }

    public Optional<HomeTimelineCache> getHomeTimeline(@NonNull String id,
                                                       @NonNull Pageable page) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        if (existsById(id))
            throw new RuntimeException(
                    String.format("%s doesn't exist.", id)
            );

        Set<String> jweetIds = redisZSets().reverseRange(
                id, page.getOffset(), page.getPageSize());

        try {
            if (Objects.requireNonNull(jweetIds).isEmpty()) {
                return Optional.empty();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }

        Set<JweetCache> jweets = new HashSet<>();
        jweetCacheRepository.findAllById(jweetIds).forEach(jweet -> {
            jweet.ifPresent(jweets::add);
        });
        return Optional.of(new HomeTimelineCache(id, jweets));
    }

    public boolean existsById(@NonNull String id) {
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

    public Long size(@NonNull String id) {
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

    public void deleteById(@NonNull String id) {
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

    public void deleteAllById(@NonNull Iterable<? extends String> strings) {
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
