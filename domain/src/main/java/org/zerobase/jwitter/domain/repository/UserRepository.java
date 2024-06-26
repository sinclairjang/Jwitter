package org.zerobase.jwitter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerobase.jwitter.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
