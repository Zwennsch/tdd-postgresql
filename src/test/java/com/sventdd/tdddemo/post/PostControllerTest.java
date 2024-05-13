package com.sventdd.tdddemo.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    // Needed to perform/mock all sorts of HttpRequests
    @Autowired
    MockMvc mockMvc;

    // Mocks the PostRepository
    @MockBean
    PostRepository postRepository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setup() {
        posts = List.of(
                new Post(1, 1, "Hello World!", "This is my first post", null),
                new Post(2, 2, "Second Post!", "This is my second post", null));
    }

    // REST API

    // list
    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello World!",
                        "body":"This is my first post",
                        "version":null
                    },
                    {
                        "id":2,
                        "userId":2,
                        "title":"Second Post!",
                        "body":"This is my second post",
                        "version":null
                    }
                ]
                """;

        // this is the mocking:
        when(postRepository.findAll()).thenReturn(posts);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    // /api/posts/1
    @Test
    void shouldFindFirstPost() throws Exception {
        String jsonResponse = """

                {
                    "id":1,
                    "userId":1,
                    "title":"Hello World!",
                    "body":"This is my first post",
                    "version":null
                }

                """;

        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    // /api/posts/999
}
