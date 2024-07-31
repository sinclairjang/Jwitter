package org.zerobase.jwitter.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserTimeline {
    @Id
    private Long userId;
}
