package org.zerobase.jwitter.domain.repository.cache;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.cache.SessionToken;

import java.util.Optional;

@EnableRedisRepositories
@Repository
public interface SessionTokenRepository
        extends CrudRepository<SessionToken, String> {
    Optional<SessionToken> findByToken(String token);

    default SessionToken refreshToken(String token) {
        Optional<SessionToken> sessionToken = this.findByToken(token);
        if (sessionToken.isEmpty()) {
            throw new RuntimeException(
                    String.format("Session token:%s doesn't exist.", token)
            );
        }
        SessionToken newSessionToken = SessionToken.copyOf(sessionToken.get());
        this.delete(sessionToken.get());
        return this.save(newSessionToken);
    }
}
