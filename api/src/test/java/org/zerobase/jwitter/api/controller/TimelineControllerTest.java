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
import org.springframework.test.web.servlet.MockMvc;
import org.zerobase.jwitter.domain.model.User;
import org.zerobase.jwitter.domain.repository.cache.HomeTimelineCacheRepository;
import org.zerobase.jwitter.domain.repository.cache.JweetCacheRepository;

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

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class TimelineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JweetCacheRepository jweetCacheRepository;

    @Autowired
    private HomeTimelineCacheRepository homeTimelineCacheRepository;

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
                .andExpect(status().isOk())
                .andDo(result -> {
                    authKeymap.put(young.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                                    .split(" ")[1]);
                });
        String hansJson = FileUtil.readFromFileToString("/users/hans.json");
        User hans = MapperUtil.deserializeUser(hansJson);
        mockMvc.perform(post("/signup")
                        .content(hansJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    authKeymap.put(hans.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                                    .split(" ")[1]);
                });
        String averyJson = FileUtil.readFromFileToString("/users/avery.json");
        User avery = MapperUtil.deserializeUser(averyJson);
        mockMvc.perform(post("/signup")
                        .content(averyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    authKeymap.put(avery.getUsername(),
                            Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                                    .split(" ")[1]);
                });
        assertFalse(authKeymap.isEmpty());
        log.info("Test session token registry: {}", authKeymap.toString());
    }

    @Order(Integer.MIN_VALUE)
    @Test
    void avery_follows_hans() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "3")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("avery")))
                .andExpect(status().isOk());
    }

    @Order(Integer.MIN_VALUE)
    @Test
    void young_follows_hans() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/follow")
                        .param("followerId", "1")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("young")))
                .andExpect(status().isOk());
    }

    @RepeatedTest(3)
    @Order(Integer.MIN_VALUE + 1)
    void hans_posts_three_jweets() throws Exception {
        String json = "{\"authorId\":2, \"text\":\"Y'all! I'm Hans.\"}";
        mockMvc.perform(post("http://localhost:8080/v1/jweet")
                        .header("Authorization", authKeymap.get("hans"))
                        .content(json)
                        .contentType((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
    }

    @Order(100)
    @Test
    void avery_sees_his_timeline_with_hans_three_jweets() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/home_timeline/3")
                        .param("cursor", "0")
                        .header("Authorization", authKeymap.get("avery"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").exists())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(100)
    @Test
    void young_sees_his_timeline_with_hans_three_jweets() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/home_timeline/1")
                        .param("cursor", "0")
                        .header("Authorization", authKeymap.get("young"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").exists())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(200)
    @Test
    void avery_unfollows_hans() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/social/unfollow")
                        .param("followerId", "3")
                        .param("followeeId", "2")
                        .header("Authorization", authKeymap.get("avery")))
                .andExpect(status().isOk());
    }

    @Order(201)
    @Test
    void hans_posts_another_jweet() throws Exception {
        String json = "{\"authorId\":2, \"text\":\"Can you hear me?\"}";
        mockMvc.perform(post("http://localhost:8080/v1/jweet")
                        .header("Authorization", authKeymap.get("hans"))
                        .content(json)
                        .contentType((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
    }

    @Order(202)
    @Test
    void avery_doent_see_hans_new_jweet_now() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/home_timeline/3")
                        .param("cursor", "0")
                        .header("Authorization", authKeymap.get("avery"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").exists())
                .andExpect(jsonPath("$.content[3]").doesNotExist())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(202)
    @Test
    void young_sees_hans_new_jweet_at_the_top() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/home_timeline/1")
                        .param("cursor", "0")
                        .header("Authorization", authKeymap.get("young"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("jweet:4"))
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").exists())
                .andExpect(jsonPath("$.content[3]").exists())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @AfterAll
    void clean() throws Exception {
        jweetCacheRepository.deleteAll();
        homeTimelineCacheRepository.deleteAll();
    }
}