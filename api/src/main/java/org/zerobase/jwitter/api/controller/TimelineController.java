package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zerobase.jwitter.api.service.HomeTimelineService;
import org.zerobase.jwitter.api.service.UserTimelineService;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

@RequiredArgsConstructor
@RestController
public class TimelineController {
    private final HomeTimelineService homeTimelineService;
    private final UserTimelineService userTimelineService;

    @Value("${spring.timeline.buffer-size}")
    private String bufferSize;

    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #id, '')")
    @GetMapping("/v1/home_timeline/{id}")
    public PageImpl<JweetCache> getHomeTimeline(@PathVariable Long id,
                                                @RequestParam int cursor) {
        return homeTimelineService.getHomeTimeline(id,
                PageRequest.of(cursor, Integer.parseInt(bufferSize)));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/v1/user_timeline/{id}")
    public Page<Jweet> geUserTimeline(@PathVariable Long id,
                                      @RequestParam int cursor) {
        return userTimelineService.getUserTimeline(id,
                PageRequest.of(cursor, Integer.parseInt(bufferSize),
                        Sort.by("createdAt").descending()));
    }
}
