package org.zerobase.jwitter.domain.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.zerobase.jwitter.domain.model.cache.JweetCache.prefix;

@RequiredArgsConstructor
@org.springframework.stereotype.Repository
public class JweetCacheRepository implements Repository<JweetCache, String> {
    private final RedisTemplate<String, String> template;

    private HashOperations<String, String, String> redisHash() {
        return template.opsForHash();
    }

    @Transactional
    public void save(@NotNull JweetCache jweet) {
            redisHash().put(jweet.getId(), "author_id", jweet.getAuthorId());
            redisHash().put(jweet.getId(), "text", jweet.getText());
            redisHash().put(jweet.getId(), "likes", jweet.getLikes());
            redisHash().put(jweet.getId(), "created_at", jweet.getCreatedAt());
    }

    @Transactional
    public void updateText(@NotNull JweetCache jweet) {
        redisHash().put(jweet.getId(), "text", jweet.getText());
    }

    @Transactional
    public void updateLikes(@NotNull JweetCache jweet) {
        redisHash().put(jweet.getId(), "likes", jweet.getLikes());
    }


    public Optional<JweetCache> findById(@NotNull String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        try {
            Map<String, String> entries = redisHash().entries(id);
            Objects.requireNonNull(entries);
            JweetCache jweet =
                    JweetCache.builder()
                            .id(id)
                            .authorId(entries.get("author_id"))
                            .text(entries.get("text"))
                            .likes(entries.get("likes"))
                            .createdAt(entries.get("created_at"))
                            .build();
            Objects.requireNonNull(jweet);
            return Optional.of(jweet);
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "NullPointerException occured. Possibly due to transaction/pipeline"
            );
        }
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

    public Iterable<Optional<JweetCache>> findAllById(@NotNull Iterable<String> ids) {
        Set<Optional<JweetCache>> jweets = new HashSet<>();
        ids.forEach(id -> {
            jweets.add(findById(id));
        });
        return jweets;
    }

    public Long size(@NotNull String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;

        if (!existsById(id))
            throw new RuntimeException(
                    String.format("%s doesn't exist.", id)
            );

        try {
            Long size = redisHash().size(id);
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
            Long deleted = redisHash().delete(
                    id, "author_id", "text", "likes", "created_at");
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
