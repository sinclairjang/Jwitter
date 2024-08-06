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
import org.zerobase.jwitter.api.dto.JweetCommentDto;
import org.zerobase.jwitter.api.dto.JweetCommentPageDto;
import org.zerobase.jwitter.api.dto.JweetDto;
import org.zerobase.jwitter.api.service.JweetCRUDService;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.JweetComment;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@Api(tags = {"Jweet Crud APIs"})
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/jweet")
public class JweetController {
    private final JweetCRUDService jweetCRUDService;
    @ApiOperation("Read Jweet")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 404,
                            message =  "Requested Jweet is not found"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<JweetDto.Out> readJweet(
            @PathVariable
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id) throws ParseException {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetDto.Out.fromEntity(jweetCRUDService.readJweet(id)));
    }

    @ApiOperation("Post Jweet")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 201,
                            message = "Jweet is posted successfully",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweet, '')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JweetDto.Out> postJweet(
            @RequestBody @Valid
            @ApiParam(name = "Json", value = "Jweet post request", required = true)
            JweetDto.CIn jweetDto) {
        Jweet jweet = jweetCRUDService.postJweet(JweetDto.CIn.toEntity(jweetDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetDto.Out.fromEntity(jweet));
    }

    @ApiOperation("Edit Jweet")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Jweet is updated successfully",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #jweetId, 'Jweet', '')")
    @PutMapping("/{id}")
    public ResponseEntity<JweetDto.Out> editJweet(
            @PathVariable
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id,
            @RequestBody @Valid
            @ApiParam(name = "Json", value = "Jweet update request", required = true)
            JweetDto.UIn jweetDto) {
        Jweet jweet = jweetCRUDService.editJweet(id, jweetDto.getText());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetDto.Out.fromEntity(jweet));
    }

    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Jweet is deleted successfully"
                    ),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Delete Jweet")
    @PreAuthorize("@writeJweetPermission.hasPermission(authentication, #id, 'Jweet', '')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteJweet(
            @PathVariable
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id) {
        jweetCRUDService.deleteJweet(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(null);
    }

    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Jweet is liked successfully",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Add one like to selected Jweet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("{id}/like")
    public ResponseEntity<JweetDto.Out> likeJweet(
            @PathVariable
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            Long id) {
        Jweet jweet = jweetCRUDService.likeJweet(id);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetDto.Out.fromEntity(jweet));
    }

    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Jweet comment is read successfully",
                            response = JweetCommentPageDto.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Read the comments of selected Jweet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}/comment")
    public ResponseEntity<JweetCommentPageDto> readJweetComments(
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            @PathVariable Long id,
            @ApiParam(name = "page", value = "Page",
                    example = "3", required = true)
            @RequestParam int page,
            @ApiParam(name = "size", value = "Size",
                    example = "5", required = true)
            @RequestParam int size
    ) {
        Page<JweetComment> pageResult =
                jweetCRUDService.readJweetComments(id,
                PageRequest.of(page,
                size));
        List<JweetCommentDto.Out> jweetCommentDtos =
                pageResult.map(JweetCommentDto.Out::fromEntity).toList();
        JweetCommentPageDto jweetCommentPageDto = JweetCommentPageDto.builder()
                .content(jweetCommentDtos)
                .totalPage(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .build();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jweetCommentPageDto);
    }
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 201,
                            message = "Jweet comment is created successfully",
                            response = JweetCommentDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Post the comment to selected Jweet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JweetCommentDto.Out> postJweetComment(
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @Valid JweetCommentDto.CIn jweetCommentDto) {
        JweetComment jweetComment = jweetCRUDService.postJweetComment(id,
                JweetCommentDto.CIn.toEntity(jweetCommentDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetCommentDto.Out.fromEntity(jweetComment));
    }

    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Jweet comment is updated successfully",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Edit the comment of selected Jweet")
    @PreAuthorize("@writeJweetCommentPermission.hasPermission(authentication, #jweetComment, '')")
    @PutMapping("/{jweetId}/comment/{commentId}")
    public ResponseEntity<JweetCommentDto.Out> editJweetComment(
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "23", required = true)
            @PathVariable Long jweetId,
            @ApiParam(name = "id", value = "Commenter Id",
                    allowableValues = "range[1, infinity]",
                    example = "3721", required = true)
            @PathVariable Long commentId,
            @ApiParam(name = "Json", value = "Jweet comment update request",
                    required = true)
            @RequestBody JweetCommentDto.UIn jweetCommentDto
    ) {
        JweetComment jweetComment = jweetCRUDService.editJweetComment(
                jweetId,
                commentId,
                jweetCommentDto.getText());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(JweetCommentDto.Out.fromEntity(jweetComment));
    }

    @ApiResponses(
            {
                    @ApiResponse(
                            code = 204,
                            message = "Jweet comment is deleted successfully"
                    ),
                    @ApiResponse(
                            code = 400,
                            message =  "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message =  "Internal server error"
                    )
            }
    )
    @ApiOperation("Delete the comment from selected Jweet")
    @PreAuthorize("@writeJweetCommentPermission.hasPermission(authentication, #commentId, 'JweetComment', '')")
    @DeleteMapping("/{jweetId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteJweetComment(
            @ApiParam(name = "id", value = "Jweet Id",
                    allowableValues = "range[1, infinity]",
                    example = "23", required = true)
            @PathVariable Long jweetId,
            @ApiParam(name = "id", value = "Comment Id",
                    allowableValues = "range[1, infinity]",
                    example = "101", required = true)
            @PathVariable  Long commentId) {
        jweetCRUDService.deleteJweetComment(jweetId, commentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(null);
    }
}
