package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.zerobase.jwitter.domain.model.Jweet;

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
        @ApiModelProperty(name = "Author Id", example = "1", required = true)
        private Long authorId;

        @Size(max = 280)
        @ApiModelProperty(name = "Text", example = "Hello!", required = true)
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

        @Size(max = 280)
        @ApiModelProperty(name = "Text", example = "Hello!", required = true)
        private String text;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Jweet CRU response")
    public static class Out {
        @ApiModelProperty(name = "Jweet Id", example = "1", required = true)
        private Long id;

        @ApiModelProperty(name = "Author Id", example = "1", required = true)
        private Long authorId;

        @Size(max = 280)
        @ApiModelProperty(name = "Text", example = "Hello!", required = true)
        private String text;

        @Min(0)
        @ApiModelProperty(name = "Likes", example = "0", required = true)
        private Long likes;

        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
        @ApiModelProperty(name = "Created At", example = "2018-02-21 12:00:00 PM", required = true)
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
    }

}
