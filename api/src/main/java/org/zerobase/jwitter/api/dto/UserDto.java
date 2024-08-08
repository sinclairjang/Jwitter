package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.zerobase.jwitter.api.aop.validation.Password;
import org.zerobase.jwitter.domain.model.Role;
import org.zerobase.jwitter.domain.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserDto implements Serializable {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("User login request")
    public static class LIn {
        @NotNull
        @Size(min = 2, max = 48)
        @ApiModelProperty(value = "User Name", example = "gomawoe", required = true)
        private String username;

        @NotNull
        @Size(min = 8, max = 20)
        @ApiModelProperty(value = "Password", example = "1004nalgae", required = true)
        private String password;
    }

    @Password
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("User signup request")
    public static class SIn {
        @NotNull
        @Size(min = 2, max = 48)
        @ApiModelProperty(value = "User Name", example = "gomawoe", required = true)
        private String username;

        @NotNull
        @Email(regexp = ".*@.*\\..*")
        @ApiModelProperty(value = "Email", example = "sinclairjang@gmail.com", required = true)
        private String email;

        @NotNull
        @Size(min = 8, max = 20)
        @ApiModelProperty(value = "Password", example = "1004nalgae", required = true)
        private String password;

        @NotNull
        @ApiModelProperty(value = "Password Confirmation", example = "1004nalgae", required = true)
        private String passwordConfirm;

        @Singular
        @NotNull
        @ApiModelProperty(value = "Roles",
                example = "[        {\n" +
                "            \"name\": \"admin\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"user\"\n" +
                "        }]",
                required = true)
        private Set<Role> roles = new HashSet<>();

        public static User toEntity(SIn userDto) {
            return User.builder()
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .password(userDto.getPassword())
                    .passwordConfirm(userDto.getPasswordConfirm())
                    .roles(userDto.getRoles())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("User information")
    public static class Out {
        @NotNull
        @Min(1)
        @ApiModelProperty(value = "User Id", example = "137", required = true)
        private Long id;

        @NotNull
        @Size(min = 2, max = 48)
        @ApiModelProperty(value = "User Name", example = "gomawoe", required = true)
        private String username;

        @NotNull
        @Email(regexp = ".*@.*\\..*")
        @ApiModelProperty(value = "Email", example = "sinclairjang@gmail.com", required = true)
        private String email;

        public static UserDto.Out from(User user) {
            return UserDto.Out.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @ApiModel("Another user information")
    public static class Out2 {
        @NotNull
        @Min(1)
        @ApiModelProperty(value = "User Id", example = "3412", required = true)
        private Long id;

        @NotNull
        @Size(min = 2, max = 48)
        @ApiModelProperty(value = "User Name", example = "samuel", required = true)
        private String username;

        @NotNull
        @Email(regexp = ".*@.*\\..*")
        @ApiModelProperty(value = "Email", example = "sam0124@gmail.com", required = true)
        private String email;

        public static UserDto.Out2 from(User user) {
            return UserDto.Out2.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
    }

}
