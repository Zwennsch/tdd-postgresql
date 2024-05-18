package com.sventdd.tdddemo.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostRepository postRepository;

    PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    List<Post> findAll() {
        return postRepository.findAll();
    }
    
    @GetMapping("/{id}")
    Optional<Post> findPostById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }
    

}
