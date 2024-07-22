package org.zerobase.jwitter.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.zerobase.jwitter.domain.model.Jweet;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.model.UserTimeline;

@RequiredArgsConstructor
@org.springframework.stereotype.Repository
public class UserTimelineRepository implements Repository<UserTimeline, Long> {
    private final JweetRepository jweetRepository;
    public Page<Jweet> getUserTimeline(UserTimeline userTimeline,
                                       Pageable page) {

        return jweetRepository.findAllByAuthor(
                new User(userTimeline.getUserId()), page);
    }
}
