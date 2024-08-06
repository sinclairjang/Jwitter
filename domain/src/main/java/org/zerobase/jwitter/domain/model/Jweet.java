package org.zerobase.jwitter.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonAppend(attrs = {
        @JsonAppend.Attr(value = "jweetComments")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(value = {"author", "jweetComments"}, allowSetters = true)
@Data
@Entity
@Table(name = "JWEETS")
public class Jweet implements Serializable {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;

    @NotNull
    @Column(name = "author_id")
    private Long authorId;

    @NotNull
    @Size(max = 280)
    private String text;

    @NotNull
    @Min(0)
    private Long likes = 0L;

    @NotNull
    @Column(updatable = false)
    private Long createdAt; // unix time

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "jweet",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    @Column(name = "comment_id")
    private Set<JweetComment> jweetComments = new HashSet<>();

    public void addJweetComments(JweetComment jweetComment) {
        this.jweetComments.add(jweetComment);
        jweetComment.setJweetId(this.id);
    }

    @PrePersist
    public void createdAt() {
        this.createdAt = System.currentTimeMillis() / 1000L;
    }
}
