package org.zerobase.jwitter.api.aop.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerobase.jwitter.api.aop.exception.TokenTypeException;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TokenFilter extends OncePerRequestFilter {
    private final SessionTokenRepository sessionTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
        } else {
            String tokenType = token.split(" ")[0];
            if (!tokenType.equals("Bearer")) {
                throw new TokenTypeException(
                        HttpStatus.BAD_REQUEST,
                        String.format("{%s} is not required Bearer type",
                                tokenType)
                );
            }

            String content = token.split(" ")[1];
            Optional<SessionToken> sessionToken =
                    sessionTokenRepository.findByToken(content);
            if (sessionToken.isEmpty()) {
                filterChain.doFilter(request, response);
            } else {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                sessionToken.get().getToken(),
                                null,
                                sessionToken.get().getRoles().stream()
                                        .map(e -> new SimpleGrantedAuthority(
                                                "ROLE_" + e.toString()))
                                        .collect(Collectors.toList())
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                sessionTokenRepository.refreshToken(content);
                filterChain.doFilter(request, response);
            }
        }
    }
}
