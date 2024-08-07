package org.zerobase.jwitter.api.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zerobase.jwitter.api.aop.exception.SessionTokenNotFoundException;
import org.zerobase.jwitter.api.aop.exception.UserAlreadyExistsException;
import org.zerobase.jwitter.api.dto.JweetDto;
import org.zerobase.jwitter.api.dto.UserDto;
import org.zerobase.jwitter.domain.model.Role;
import org.zerobase.jwitter.domain.model.cache.SessionToken;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.cache.SessionTokenRepository;
import org.zerobase.jwitter.domain.repository.UserRepository;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;

@Api(tags = {"User Login APIs"})
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

    @ApiOperation("Login")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 200,
                            message = "Login complete",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message = "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message = "Internal server error"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<UserDto.Out> login(
            @RequestBody
            @ApiParam(name = "Json", value = "Login request", required = true)
            UserDto.LIn userDto) {
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(), userDto.getPassword());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        String sessionToken = generateNewToken();
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User %s doesn't exist.",
                                userDto.getUsername())
                ));

        SessionToken refresh =
                sessionTokenRepository.findById(String.valueOf(user.getId()))
                        .orElseThrow(() -> new SessionTokenNotFoundException(
                                String.format(
                                        "Session Token %d doesn't exist.",
                                        user.getId()
                                )));
        refresh.setToken(sessionToken);
        sessionTokenRepository.save(refresh);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(UserDto.Out.from(user));
    }

    @ApiOperation("Signup")
    @ApiResponses(
            {
                    @ApiResponse(
                            code = 201,
                            message = "Signup complete",
                            response = JweetDto.Out.class),
                    @ApiResponse(
                            code = 400,
                            message = "Invalid request"
                    ),
                    @ApiResponse(
                            code = 500,
                            message = "Internal server error"
                    )
            }
    )
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto.Out> signup(
            @RequestBody @Valid
            @ApiParam(name = "Json", value = "Signup request", required = true)
            UserDto.SIn userDto) {
        User user = UserDto.SIn.toEntity(userDto);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(UserDto.Out.from(user));
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}
