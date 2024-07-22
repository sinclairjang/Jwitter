package org.zerobase.jwitter.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TimelineController {
    @GetMapping("/v1/home_timeline")
    public ResponseEntity<?> getHomeTimeline() {
        return null;
    }

    @GetMapping("/v1/user_timeline")
    public ResponseEntity<?> geUserTimeline() {
        return null;
    }
}
