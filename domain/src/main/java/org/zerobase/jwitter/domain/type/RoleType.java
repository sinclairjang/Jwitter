package org.zerobase.jwitter.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleType {
    ADMIN("admin"),
    USER("user");

    private final String name;
    RoleType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }
}
