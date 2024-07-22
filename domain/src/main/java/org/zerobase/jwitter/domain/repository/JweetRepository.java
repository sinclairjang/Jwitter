package org.zerobase.jwitter.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.User;

@EnableJpaRepositories
@Repository
public interface JweetRepository extends JpaRepository<Jweet, Long> {
    Page<Jweet> findAllByAuthor(User user, Pageable pageable);

}
