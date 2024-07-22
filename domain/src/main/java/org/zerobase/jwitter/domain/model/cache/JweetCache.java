package org.zerobase.jwitter.domain.model.cache;

import lombok.*;
import org.zerobase.jwitter.domain.model.Jweet;

import javax.validation.constraints.Size;

@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Data
public class JweetCache {
    public static final String prefix = "jweet:";

    @EqualsAndHashCode.Include
    private String id;

    private String authorId;

    @Size(max = 280)
    private String text;

    private String likes;

    private String createdAt; // unix time

    public static JweetCache from(Jweet jweet) {
        return JweetCache.builder()
                .id(prefix + jweet.getId())
                .authorId(String.valueOf(jweet.getAuthorId()))
                .text(jweet.getText())
                .likes(String.valueOf(jweet.getLikes()))
                .createdAt(String.valueOf(jweet.getCreatedAt()))
                .build();
    }

    public JweetCache(String id, String authorId, String text,
                      String likes, String createdAt) {
        if (!id.startsWith(prefix))
            id = prefix + id;
        this.id = id;
        this.authorId = authorId;
        this.text = text;
        this.likes = likes;
        this.createdAt = createdAt;
    }
}
