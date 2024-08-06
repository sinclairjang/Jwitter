package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.zerobase.jwitter.domain.model.JweetComment;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JweetCommentDto implements Serializable {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet comment create request")
    public static class CIn {
        @NotNull
        @ApiModelProperty(name = "Commenter Id", example = "3721", required = true)
        private Long commenterId;

        @Size(max = 280)
        @ApiModelProperty(name = "Comment", example = "I like your post!", required = true)
        private String text;

        public static JweetComment toEntity(JweetCommentDto.CIn jweetCommentDto) {
            return JweetComment.builder()
                    .commenterId(jweetCommentDto.getCommenterId())
                    .text(jweetCommentDto.getText())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet comment update request")
    public static class UIn {

        @Size(max = 280)
        @ApiModelProperty(name = "Comment", example = "I like your post!", required = true)
        private String text;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet comment CRU response")
    public static class Out {
        @ApiModelProperty(name = "Comment Id", example = "101", required = true)
        private Long id;

        @ApiModelProperty(name = "Jweet Id", example = "23", required = true)
        private Long jweetId;

        @ApiModelProperty(name = "Commenter Id", example = "3721", required = true)
        private Long commenterId;

        @Size(max = 280)
        @ApiModelProperty(name = "Comment", example = "I like your post!", required = true)
        private String text;

        @Min(0)
        @ApiModelProperty(name = "Likes", example = "0", required = true)
        private Long likes;

        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
        @ApiModelProperty(name = "Created At", example = "2018-02-21 12:00:00 PM", required = true)
        private String createdAt;

        public static JweetCommentDto.Out fromEntity(JweetComment jweetComment) {
            Date date = new Date(jweetComment.getCreatedAt() * 1000L);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            return Out.builder()
                    .id(jweetComment.getId())
                    .jweetId(jweetComment.getJweetId())
                    .commenterId(jweetComment.getCommenterId())
                    .text(jweetComment.getText())
                    .likes(jweetComment.getLikes())
                    .createdAt(df.format(date))
                    .build();
        }
    }

}
