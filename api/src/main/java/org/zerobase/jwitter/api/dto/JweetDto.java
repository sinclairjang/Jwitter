package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.cache.JweetCache;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JweetDto implements Serializable {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet create request")
    public static class CIn {
        @NotNull
        @ApiModelProperty(value = "Author Id", example = "13", required = true)
        private Long authorId;

        @NotNull
        @Size(max = 280)
        @ApiModelProperty(value = "Text", example = "Hello!", required = true)
        private String text;

        public static Jweet toEntity(JweetDto.CIn jweetDto) {
            return Jweet.builder()
                    .authorId(jweetDto.getAuthorId())
                    .text(jweetDto.getText())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet update request")
    public static class UIn {

        @NotNull
        @Size(max = 280)
        @ApiModelProperty(value = "Text", example = "Hello!", required = true)
        private String text;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet CRU response")
    public static class Out {
        @NotNull
        @Min(1)
        @ApiModelProperty(value = "Jweet Id", example = "1", required = true)
        private Long id;

        @NotNull
        @Min(1)
        @ApiModelProperty(value = "Author Id", example = "13", required = true)
        private Long authorId;

        @NotNull
        @Size(max = 280)
        @ApiModelProperty(value = "Text", example = "Hello!", required = true)
        private String text;

        @NotNull
        @Min(0)
        @ApiModelProperty(value = "Likes", example = "10", required = true)
        private Long likes;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
        @ApiModelProperty(value = "Created At", example = "2018-02-21 12:00:00 PM", required = true)
        private String createdAt;

        public static JweetDto.Out fromEntity(Jweet jweet) {
            Date date = new Date(jweet.getCreatedAt() * 1000L);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            return JweetDto.Out.builder()
                    .id(jweet.getId())
                    .authorId(jweet.getAuthorId())
                    .text(jweet.getText())
                    .likes(jweet.getLikes())
                    .createdAt(df.format(date))
                    .build();
        }

        public static JweetDto.Out fromEntity(JweetCache jweet) {
            Date date = new Date(Long.parseLong(jweet.getCreatedAt()) * 1000L);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            return JweetDto.Out.builder()
                    .id(Long.parseLong(jweet.getId().replace(JweetCache.prefix, "")))
                    .authorId(Long.valueOf(jweet.getAuthorId()))
                    .text(jweet.getText())
                    .likes(Long.valueOf(jweet.getLikes()))
                    .createdAt(df.format(date))
                    .build();
        }
    }

}
