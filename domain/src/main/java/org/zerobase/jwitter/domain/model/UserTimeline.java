package org.zerobase.jwitter.domain.model;

import lombok.Data;

import javax.persistence.Id;

@Data
public class UserTimeline {
    @Id
    private Long userId;
}
