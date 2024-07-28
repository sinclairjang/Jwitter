package org.zerobase.jwitter.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.Follow;
import org.zerobase.jwitter.domain.model.User;

import java.util.Set;

@EnableJpaRepositories
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Set<Follow> findByfollower(User follower);

    @Query(value = "SELECT * FROM FOLLOWS WHERE FOLLOWER_ID = ?1",
            countQuery = "SELECT count(*) FROM USERS WHERE FOLLOWER_ID = ?1",
            nativeQuery = true)
    Page<Follow> findByfollower(User follower, Pageable pageable);

    Set<Follow> findByfollowee(User followee);

    @Query(value = "SELECT * FROM FOLLOWS WHERE FOLLOWEE_ID = ?1",
            countQuery = "SELECT count(*) FROM USERS WHERE FOLLOWEE_ID = ?1",
            nativeQuery = true)
    Page<Follow> findByfollowee(User followee, Pageable pageable);
}
