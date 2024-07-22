package org.zerobase.jwitter.api.aop.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.repository.JweetCommentRepository;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;

import java.io.Serializable;
import java.util.Optional;

@RequiredArgsConstructor
@Component("writeJweetCommentPermission")
public class WriteJweetCommentPermission implements PermissionEvaluator {
    private final SessionTokenRepository sessionTokenRepository;
    private final JweetCommentRepository jweetCommentRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(!targetType.equals("JweetComment"))
            return false;

        Long jweetCommentId = (Long) targetId;
        JweetComment jweetComment =
                jweetCommentRepository.findById(jweetCommentId).orElseThrow(
                () -> new RuntimeException(
                        String.format("Jweet Comment %s doesn't exist.", jweetCommentId)
                )
        );
        Long commenterId = jweetComment.getCommenterId();
        Optional<SessionToken> sessionToken =
                sessionTokenRepository.findById(String.valueOf(commenterId));
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
