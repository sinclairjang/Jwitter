package org.zerobase.jwitter.domain.model.cache;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Don't directly create this object unless you are saving jweets.
 * Use {@code HomeTimelineRepository} instead.
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HomeTimelineCache {
    public static final String prefix = "home_timeline:";

    @EqualsAndHashCode.Include
    private String id;

    /**
     * Cache existing jweets with the home timeline. (likely in the
     * process of {@code postJweet} operation)
     * <p>
     * It automatically caches them to {@code JweetCacheRepository} if they're
     * not cached to guarantee the cache coherence.
     */
    private Set<JweetCache> jweets = new HashSet<>();

    public HomeTimelineCache(String id) {
        if (!id.startsWith(prefix))
            id = prefix + id;
        this.id = id;
    }

    public HomeTimelineCache(String id, Set<JweetCache> jweets) {
        if (!id.startsWith(prefix))
            id = prefix + id;
        this.id = id;
        this.jweets = jweets;
    }
}
