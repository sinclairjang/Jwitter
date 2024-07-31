package org.zerobase.jwitter.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonAppend(attrs = {
        @JsonAppend.Attr(value = "jweetComments")
})
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "author_id")
    private Long authorId;

    @Size(max = 280)
    private String text;

    private Integer likes = 0;

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
