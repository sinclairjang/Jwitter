package org.zerobase.jwitter.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    public User() {}
    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
