package org.zerobase.jwitter.api.security.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.zerobase.jwitter.domain.model.Role;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.zerobase.jwitter.domain.type.RoleType;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwitterUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                    String.format("User %s doesn't exist.",
                            username)
            ));

        UserBuilder userBuilder = org.springframework.security.core.userdetails.
                User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRoles().stream()
                            .map(e -> e.getName().getName())
                            .toArray(String[]::new));

        return userBuilder.build();
    }
}
