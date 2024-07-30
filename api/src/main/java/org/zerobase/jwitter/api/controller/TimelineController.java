package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zerobase.jwitter.api.service.HomeTimelineService;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

@RequiredArgsConstructor
@RestController
public class TimelineController {
    private final HomeTimelineService homeTimelineService;

    @Value("${spring.timeline.buffer-size}")
    private String bufferSize;

    @GetMapping("/v1/home_timeline/{id}")
    public PageImpl<JweetCache> getHomeTimeline(@PathVariable Long id,
                                                @RequestParam int cursor) {
        PageImpl<JweetCache> test = homeTimelineService.getHomeTimeline(id,
                PageRequest.of(cursor, Integer.parseInt(bufferSize)));
        return test;
    }

    @GetMapping("/v1/user_timeline")
    public ResponseEntity<?> geUserTimeline() {
        return null;
    }
}
