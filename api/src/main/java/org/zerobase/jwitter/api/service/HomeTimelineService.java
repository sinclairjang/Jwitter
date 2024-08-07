package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.zerobase.jwitter.domain.model.cache.JweetCache;
import org.zerobase.jwitter.domain.repository.cache.HomeTimelineCacheRepository;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;

@RequiredArgsConstructor
@Service
public class HomeTimelineService {
    private final HomeTimelineCacheRepository homeTimelineCacheRepository;
    private final SessionTokenRepository sessionTokenRepository;
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #id, '')")
    public Page<JweetCache> getHomeTimeline(Long id, Pageable page) {
        return homeTimelineCacheRepository.getHomeTimeline(String.valueOf(id), page);
    }
}
