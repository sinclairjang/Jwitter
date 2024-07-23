package org.zerobase.jwitter.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.User;

import java.util.Set;

@EnableJpaRepositories
@Repository
public interface JweetRepository extends JpaRepository<Jweet, Long> {
    Page<Jweet> findAllByAuthor(User user, Pageable pageable);

    @Query(value = """
                select j
                from Jweet j
                left join fetch j.jweetComments
                where j.id = :id
                """)
    Set<Jweet> findAllById(@Param("id") Long jweetId);

}
