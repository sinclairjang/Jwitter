package org.zerobase.jwitter.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerobase.jwitter.api.security.dto.UserDto;
import org.zerobase.jwitter.domain.model.Role;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;
import org.zerobase.jwitter.domain.repository.UserRepository;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@RestController
public class LoginController {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto loginRequest) {
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        String sessionToken = generateNewToken();
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User %s doesn't exist.",
                                loginRequest.getUsername())
                ));

        SessionToken refresh =
                sessionTokenRepository.findById(String.valueOf(user.getId()))
                        .orElseThrow(() -> new RuntimeException(
                                String.format(
                                        "Session Token %d doesn't exist.",
                                        user.getId()
                                )));
        refresh.setToken(sessionToken);
        sessionTokenRepository.save(refresh);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException(
                    String.format("User %s already exists.",
                            user.getUsername())
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordConfirm(user.getPassword());
        userRepository.save(user);
        String sessionToken = generateNewToken();
        sessionTokenRepository.save(
                SessionToken.builder()
                        .id(String.valueOf(user.getId()))
                        .token(sessionToken)
                        .roles(user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toList()))
                        .build());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .build();
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}
