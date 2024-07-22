package org.zerobase.jwitter.domain.model.cache;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import org.zerobase.jwitter.domain.type.RoleType;

import java.util.HashSet;
import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder
@RedisHash(value = "session_token", timeToLive = 30 * 60)
public class SessionToken {

    private String id;

    @EqualsAndHashCode.Include
    @Indexed
    private String token;

    @Singular
    private Set<RoleType> roles = new HashSet<>();
}
