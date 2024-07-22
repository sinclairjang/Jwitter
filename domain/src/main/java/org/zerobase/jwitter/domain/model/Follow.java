package org.zerobase.jwitter.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "FOLLOWS")
@Immutable
public class Follow {

    @Data
    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "follower_id")
        private Long followerId;
        @Column(name = "followee_id")
        private Long followeeId;

        public Id() {
        }

        public Id(Long followerId, Long followeeId) {
            this.followerId = followerId;
            this.followeeId = followeeId;
        }
    }

    @EqualsAndHashCode.Include
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @JoinColumn(
            name = "follower_id",
            insertable = false, updatable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(
            name = "followee_id",
            insertable = false, updatable = false)
    private User followee;

    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
        this.id.followerId = follower.getId();
        this.id.followeeId = followee.getId();
    }
}
