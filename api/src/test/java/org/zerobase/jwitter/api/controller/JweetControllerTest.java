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
import org.zerobase.jwitter.domain.repository.JweetCommentRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class JweetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JweetCommentRepository jweetCommentRepository;

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
                    JweetControllerTest.class.getResourceAsStream(filePath);
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
    void young_post_a_jweet() throws Exception {
        String json = "{\"authorId\":1, \"text\":\"G'day!\"}";
        mockMvc.perform(post("http://localhost:8080/v1/jweet")
                        .header("Authorization", authKeymap.get("young"))
                        .content(json)
                        .contentType((MediaType.APPLICATION_JSON)))
                .andExpect(status().isCreated());
    }

    @Order(101)
    @Test
    void hans_reads_young_jweet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(get("http://localhost:8080/v1/jweet/1")
                        .header("Authorization", authKeymap.get("hans"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(102)
    @Test()
    void hans_reads_not_existing_jweet() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(get("http://localhost:8080/v1/jweet/2")
                        .header("Authorization", authKeymap.get("hans"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(200)
    @Test()
    void hans_likes_young_jweet() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/jweet/1/like")
                        .header("Authorization", authKeymap.get("hans")))
                .andExpect(status().isOk());
    }

    @Order(201)
    @Test()
    void avery_sees_young_jweet_with_likes() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/jweet/1")
                        .header("Authorization", authKeymap.get("avery"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(202)
    @Test()
    void avery_likes_not_existing_jweet() throws Exception {
        mockMvc.perform(post("http://localhost:8080/v1/jweet/2/like")
                        .header("Authorization", authKeymap.get("hans")))
                .andExpect(status().isNotFound())
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(300)
    @Test
    void young_edits_jweet() throws Exception {
        String json = "{\"text\":\"Bonjour!\"}";
        mockMvc.perform(put("http://localhost:8080/v1/jweet/1")
                        .header("Authorization", authKeymap.get("young"))
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Order(301)
    @Test()
    void avery_sees_young_edited_jweet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/jweet/1")
                        .header("Authorization", authKeymap.get("avery"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Bonjour!"))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @RepeatedTest(2)
    @Order(302)
    void avery_posts_two_comments_on_young_jweet() throws Exception {
        String json = "{\"commenterId\":3,\"text\":\"Can we be friends?\"}";
        mockMvc.perform(post("http://localhost:8080/v1/jweet/1/comment")
                        .header("Authorization", authKeymap.get("avery"))
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Order(303)
    @Test()
    void young_sees_avery_comment() throws Exception {
        mockMvc.perform(get("http://localhost:8080/v1/jweet/1/comment")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Authorization", authKeymap.get("young"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].text").value("Can we be friends?"))
                .andDo(result -> log.info(result.getResponse().getContentAsString()));
    }

    @Order(304)
    @Test
    void avery_deletes_one_comment() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/v1/jweet/1/comment/1")
                        .header("Authorization", authKeymap.get("avery")))
                .andExpect(status().isNoContent());
    }

    @Order(305)
    @Test()
    void young_delets_jweet_hence_no_comments_left() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/v1/jweet/1")
                        .header("Authorization", authKeymap.get("young")))
                .andExpect(status().isNoContent());

        assertEquals(0, jweetCommentRepository.findAll().size());
    }
}