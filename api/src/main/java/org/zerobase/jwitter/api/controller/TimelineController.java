package org.zerobase.jwitter.api.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zerobase.jwitter.api.dto.JweetDto;
import org.zerobase.jwitter.api.dto.JweetPageDto;
import org.zerobase.jwitter.api.service.HomeTimelineService;
import org.zerobase.jwitter.api.service.UserTimelineService;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

@Api(tags = {"Timeline  APIs"})
@RequiredArgsConstructor
@RestController
public class TimelineController {
    private final HomeTimelineService homeTimelineService;
    private final UserTimelineService userTimelineService;

    @Value("${spring.timeline.buffer-size}")
    private String bufferSize;

    @ApiOperation("Get your home timeline")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
                    ),
                    @ApiResponse(
                            code = 400,
                            message = "Invalid request"
                    ),
                    @ApiResponse(
                            code = 401,
                            message = "Not authorized"
                    ),
                    @ApiResponse(
                            code = 500,
                            message = "Internal server error"
                    )
            }
    )
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #id, '')")
    @GetMapping("/v1/home_timeline/{id}")
    public ResponseEntity<JweetPageDto> getHomeTimeline(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id,

            @RequestParam
            @ApiParam(name = "cursor", value = "Scrolling cursor",
                    allowableValues = "range[0, infinity]",
                    example = "3", required = true)
            int cursor) {
        Page<JweetCache> pageResult =  homeTimelineService.getHomeTimeline(id,
                PageRequest.of(cursor, Integer.parseInt(bufferSize)));
        JweetPageDto jweetPageDto = JweetPageDto.builder()
                .content(pageResult.getContent().stream().map(JweetDto.Out::fromEntity).toList())
                .totalPage(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jweetPageDto);
    }

    @ApiOperation("Get user timeline")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
                    ),
                    @ApiResponse(
                            code = 400,
                            message = "Invalid request"
                    ),
                    @ApiResponse(
                            code = 401,
                            message = "Not authorized"
                    ),
                    @ApiResponse(
                            code = 500,
                            message = "Internal server error"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/v1/user_timeline/{id}")
    public ResponseEntity<JweetPageDto> geUserTimeline(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id,
            @RequestParam
            @ApiParam(name = "cursor", value = "Scrolling cursor",
                    allowableValues = "range[0, infinity]",
                    example = "3", required = true)
            int cursor) {
        Page<Jweet> pageResult = userTimelineService.getUserTimeline(id,
                PageRequest.of(cursor, Integer.parseInt(bufferSize),
                        Sort.by("createdAt").descending()));
        JweetPageDto jweetPageDto = JweetPageDto.builder()
                .content(pageResult.getContent().stream().map(JweetDto.Out::fromEntity).toList())
                .totalPage(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jweetPageDto);
    }
}
