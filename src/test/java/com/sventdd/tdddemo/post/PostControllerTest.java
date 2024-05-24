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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    void shouldFindWithGivenID() throws Exception {
        // This does not work in my vscode setting though I am using java 21
        // var post = posts.get(0);
        // I cannot do "id":\{post.id()} in the String format
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
    @Test
    void shouldReturnNotFoundForId999() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateANewPostWhenPostIsValid() throws Exception {

        String newJsonPost = """
                {
                    "id":3,
                    "userId":1,
                    "title":"Hello new Post!",
                    "body":"This is my new post",
                    "version":null
                }
                """;
        // I am not sure why I would need this, as the test runs without this line.
        when(postRepository.save(posts.get(0))).thenReturn(posts.get(0));

        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(newJsonPost))
                .andExpect(status().isCreated());

    }

    @Test
    void shouldNotCreateANewPostWhenPostIsInvalid() throws Exception {

        String newJsonPost = """
                {
                    "id":3,
                    "userId":1,
                    "title":"",
                    "body":"",
                    "version":null
                }
                """;

        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(newJsonPost))
                .andExpect(status().isBadRequest());

    }

    // Update and delete
    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        Post updated = new Post(1, 1, "Updated title", "My Updated Post1", 1);
        String updatedJson = """
                {
                    "id":1,
                    "userId":1,
                    "title":"Updated title",
                    "body":"My updated Post1",
                    "version":1
                }
                """;
        when(postRepository.save(updated)).thenReturn(updated);
        // This mocking is important otherwise a 404 is returned 
        when(postRepository.findById(1)).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/posts/1")
                .contentType("application/json")
                .content(updatedJson))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePostWhenGivenValidId() throws Exception{
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1")
        ).andExpect(status().isNoContent());

        // make sure that the method gets called
        verify(postRepository, times(1)).deleteById(1);
    }
}
