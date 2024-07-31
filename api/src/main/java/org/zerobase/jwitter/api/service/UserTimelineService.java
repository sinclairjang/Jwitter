package org.zerobase.jwitter.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.UserTimeline;
import org.zerobase.jwitter.domain.repository.UserTimelineRepository;

@Cacheable("user_timeline")
@RequiredArgsConstructor
@Service
public class UserTimelineService {
    private final UserTimelineRepository userTimelineRepository;
    public Page<Jweet> getUserTimeline(Long id, Pageable page) {
        return userTimelineRepository.getUserTimeline(new UserTimeline(id), page);
    }
}
