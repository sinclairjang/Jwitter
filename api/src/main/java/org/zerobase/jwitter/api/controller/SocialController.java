package org.zerobase.jwitter.api.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zerobase.jwitter.api.dto.FollowDto;
import org.zerobase.jwitter.api.dto.UserDto;
import org.zerobase.jwitter.api.dto.UserPageDto;
import org.zerobase.jwitter.api.service.FollowService;
import org.zerobase.jwitter.domain.model.Follow;
import org.zerobase.jwitter.domain.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = {"Social  APIs"})
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/social")
public class SocialController {
    private final FollowService followService;

    @ApiOperation("Get all the users you follow")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
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
    @GetMapping("/all_followees/{id}")
    public ResponseEntity<Set<UserDto.Out>> getFollowees(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "10789", required = true)
            Long id) {
        Set<User> followees = followService.getFollowees(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(followees.stream()
                        .map(UserDto.Out::from)
                        .collect(Collectors.toSet()));
    }

    @ApiOperation("Get some of the users you follow")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
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
    @GetMapping("/followees/{id}")
    public ResponseEntity<UserPageDto> getFolloweesByPage(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "10789", required = true)
            Long id,
            @RequestParam
            @ApiParam(name = "page", value = "Page",
                    example = "3", required = true)
            int page,
            @RequestParam
            @ApiParam(name = "size", value = "Size",
                    example = "5", required = true)
            int size
    ) {
        Page<Follow> pageResult = followService.getFollowees(id,
                PageRequest.of(page, size));
        List<UserDto.Out> followees =
                pageResult.stream().map(Follow::getFollowee).collect(Collectors.toSet())
                        .stream().map(UserDto.Out::from).toList();
        UserPageDto userPageDto = UserPageDto.builder()
                .content(followees)
                .totalPage(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userPageDto);
    }

    @ApiOperation("Get all the users who follow you")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
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
    @GetMapping("/all_followers/{id}")
    public ResponseEntity<Set<UserDto.Out>> getFollowers(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "10789", required = true)
            Long id) {
        Set<User> followers = followService.getFollowers(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(followers.stream()
                        .map(UserDto.Out::from)
                        .collect(Collectors.toSet()));
    }

    @ApiOperation("Get some of the users who follow you")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Request successful"
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
    @GetMapping("/followers/{id}")
    public ResponseEntity<UserPageDto> getFollowersByPage(
            @PathVariable
            @ApiParam(name = "id", value = "User Id",
                    allowableValues = "range[1, infinity]",
                    example = "10789", required = true)
            Long id,
            @RequestParam
            @ApiParam(name = "page", value = "Page",
                    example = "3", required = true)
            int page,
            @RequestParam
            @ApiParam(name = "size", value = "Size",
                    example = "5", required = true)
            int size
    ) {
        Page<Follow> pageResult = followService.getFollowers(id, PageRequest.of(page, size));
        List<UserDto.Out> followers =
                pageResult.stream().map(Follow::getFollower).collect(Collectors.toSet())
                        .stream().map(UserDto.Out::from).toList();
        UserPageDto userPageDto = UserPageDto.builder()
                .content(followers)
                .totalPage(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userPageDto);
    }

    @ApiOperation("Follow another user")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 201,
                            message = "Follow successful"
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
    @org.zerobase.jwitter.api.aop.validation.Follow
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #followerId, '')")
    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FollowDto> follow(
            @RequestParam
            @ApiParam(name = "followerId", value = "Follower Id",
                    allowableValues = "range[1, infinity]",
                    example = "137", required = true)
            Long followerId,
            @RequestParam
            @ApiParam(name = "followeeId", value = "Followee Id",
                    allowableValues = "range[1, infinity]",
                    example = "3412", required = true)
            Long followeeId) {
        Follow followInfo = followService.follow(followerId, followeeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(FollowDto.from(followInfo));
    }

    @ApiOperation("Unfollow another user")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 204,
                            message = "Unfollow successful"
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
    @org.zerobase.jwitter.api.aop.validation.Follow
    @PreAuthorize("@sessionOwnerPermission.hasPermission(authentication, #followerId, '')")
    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> unfollow(
            @RequestParam
            @ApiParam(name = "followerId", value = "Follower Id",
                    allowableValues = "range[1, infinity]",
                    example = "137", required = true)
            Long followerId,
            @RequestParam
            @ApiParam(name = "followeeId", value = "Followee Id",
                    allowableValues = "range[1, infinity]",
                    example = "3412", required = true)
            Long followeeId
    ) {
        followService.unfollow(followerId, followeeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(null);
    }
}
