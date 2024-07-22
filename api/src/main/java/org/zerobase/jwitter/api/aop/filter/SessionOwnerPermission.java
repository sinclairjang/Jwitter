package org.zerobase.jwitter.api.aop.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;

import java.io.Serializable;
import java.util.Optional;

@RequiredArgsConstructor
@Component("sessionOwnerPermission")
public class SessionOwnerPermission implements PermissionEvaluator {
    private final SessionTokenRepository sessionTokenRepository;
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Long userId = (Long) targetDomainObject;
        Optional<SessionToken> sessionToken =
                sessionTokenRepository.findById(String.valueOf(userId));
        if (sessionToken.isEmpty()) {
            return false;
        } else {
             if (authentication.getPrincipal().equals(sessionToken.get().getToken())) {
                 return authentication.getAuthorities().stream().anyMatch(e -> {
                     return e.getAuthority().equals("ROLE_USER") ||
                             e.getAuthority().equals("ROLE_ADMIN");
                 });
             } else {
                 return false;
             }
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
