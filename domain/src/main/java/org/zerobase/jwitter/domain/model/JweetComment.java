package org.zerobase.jwitter.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(value = {"id", "commenter", "jweet"}, allowSetters = true)
@Data
@Entity
@Table(name = "JWEET_COMMENTS")
public class JweetComment {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id", insertable = false, updatable = false)
    private User commenter;

    @NotNull
    @Column(name = "commenter_id")
    private Long commenterId;

    @Size(max = 280)
    private String text;

    private Long likes = 0L;

    private Long createdAt; // unix time

    @ManyToOne(targetEntity = Jweet.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "jweet_id", insertable = false, updatable = false)
    private Jweet jweet;

    @Column(name = "jweet_id")
    private Long jweetId;

    @PrePersist
    public void createdAt() {
        this.createdAt = System.currentTimeMillis() / 1000L;
    }
}
