package org.zerobase.jwitter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.Follow;
import org.zerobase.jwitter.domain.model.User;

import java.util.Set;

@EnableJpaRepositories
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Set<Follow> findByfollower(User follower);
    Set<Follow> findByfollowee(User followee);
}
