package org.zerobase.jwitter.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(value = {"id", "password", "passwordConfirm", "roles"}, allowSetters = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @NotNull
    @Size(min = 2, max = 48)
    @ToString.Include
    private String username;

    @NotNull
    @Email(regexp = ".*@.*\\..*")
    @EqualsAndHashCode.Include
    @ToString.Include
    private String email;

    @NotNull
    private String password;

    @Transient
    private String passwordConfirm;

    @Singular
    @ElementCollection
    @CollectionTable(name = "ROLES")
    @AttributeOverride(
            name = "name",
            column = @Column(name = "ROLE", nullable = false)
    )
    Set<Role> roles = new HashSet<>();

    public User(Long userId) {
        this.id = userId;
    }
}
