package org.zerobase.jwitter.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.zerobase.jwitter.domain.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class SocialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Map<String, String> authKeymap = new HashMap<>();

    private static class FileUtil {

        public static String readFromFileToString(String filePath) throws IOException {
            File resource = new ClassPathResource(filePath).getFile();
            byte[] byteArray = Files.readAllBytes(resource.toPath());
            return new String(byteArray);
        }
    }

    public static class MapperUtil {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static List<User> deserializeUsers(String filePath) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
            };
            InputStream inputStream =
                    SocialControllerTest.class.getResourceAsStream(filePath);
            return objectMapper.readValue(inputStream, typeReference);
        }

        public static User deserializeUser(String json) throws JsonProcessingException {
            return objectMapper.readValue(json, User.class);
        }
    }

    @BeforeAll
    void setup() throws Exception {
        String youngJson = FileUtil.readFromFileToString("/users/young.json");
        User young = MapperUtil.deserializeUser(youngJson);
        mockMvc.perform(post("/signup")
                        .content(youngJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    authKeymap.put(young.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization")));
                });
        String hansJson = FileUtil.readFromFileToString("/users/hans.json");
        User hans = MapperUtil.deserializeUser(hansJson);
        mockMvc.perform(post("/signup")
                        .content(hansJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    authKeymap.put(hans.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization")));
                });
        String averyJson = FileUtil.readFromFileToString("/users/avery.json");
        User avery = MapperUtil.deserializeUser(averyJson);
        mockMvc.perform(post("/signup")
                        .content(averyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    authKeymap.put(avery.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization")));
                });
        assertFalse(authKeymap.isEmpty());
        log.info("Test session token registry: {}", authKeymap.toString());
    }

    @Order(100)
    @Test
    void young_follows_hans() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "1")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("young")))
                .andExpect(status().isCreated());
    }

    @Order(101)
    @Test
    void young_is_not_authorized_to_follow_hans_as_avery() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "3")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("young")))
                .andExpect(status().isForbidden());
    }

    @Order(102)
    @Test
    void young_cant_follow_himself() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "1")
                        .param("followeeId", "1")
                        .header("Authorization", authKeymap.get("young")))
                .andExpect(status().isBadRequest())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(103)
    @Test
    void young_sees_his_followee_hans() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/social/all_followees/1")
                        .header("Authorization", authKeymap.get("young"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("hans"))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(104)
    @Test
    void young_sees_his_followee_hans_by_page() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/social/followees/1")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", authKeymap.get("young"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].username").value("hans"))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(200)
    @Test
    void avery_follows_hans() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "3")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("avery")))
                .andExpect(status().isCreated());
    }

    @Order(201)
    @Test
    void avery_sees_no_followers() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/social/all_followers/3")
                        .header("Authorization", authKeymap.get("avery"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("[]", result.getResponse().getContentAsString()))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(202)
    @Test
    void hans_sees_his_followers_young_and_avery() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/social/all_followers/2")
                        .header("Authorization", authKeymap.get("hans"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[1].username").exists())
                .andExpect(jsonPath("$[2].username").doesNotExist())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }
}