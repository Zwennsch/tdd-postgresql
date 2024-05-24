package com.sventdd.tdddemo.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    Post createPost(@RequestBody @Valid Post post) {
       return postRepository.save(post);
    }

    @PutMapping("/{id}")
    Post updatePost(@PathVariable Integer id, @RequestBody @Valid Post post) {
        Optional<Post> existing = postRepository.findById(id);
        if(existing.isPresent()){
            Post updated = new Post(existing.get().id(), existing.get().userId(), post.title(), post.body(), post.version());
            return postRepository.save(updated);
        }else{
            throw new PostNotFoundException();
        }   
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deletePost(@PathVariable Integer id){
           postRepository.deleteById(id); 
    }

    
    

}
