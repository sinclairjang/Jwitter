package org.zerobase.jwitter.api.aop.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.zerobase.jwitter.api.dto.JweetDto;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.repository.JweetRepository;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;

import java.io.Serializable;
import java.util.Optional;

@RequiredArgsConstructor
@Component("writeJweetPermission")
public class WriteJweetPermission implements PermissionEvaluator {
    private final SessionTokenRepository sessionTokenRepository;
    private final JweetRepository jweetRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        JweetDto.CIn jweet = (JweetDto.CIn) targetDomainObject;
        Long authorId = jweet.getAuthorId();
        Optional<SessionToken> sessionToken =
                sessionTokenRepository.findById(String.valueOf(authorId));
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
        if (!targetType.equals("Jweet"))
            return false;

        Long jweetId = (Long) targetId;
        Jweet jweet =
                jweetRepository.findById(jweetId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet Comment %s doesn't exist.", jweetId)
                )
        );
        Long authorId = jweet.getAuthorId();
        Optional<SessionToken> sessionToken =
                sessionTokenRepository.findById(String.valueOf(authorId));
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
}
