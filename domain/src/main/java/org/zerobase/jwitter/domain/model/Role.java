package org.zerobase.jwitter.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerobase.jwitter.domain.type.RoleType;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Role {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType name;
}
