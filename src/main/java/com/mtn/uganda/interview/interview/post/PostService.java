package com.mtn.uganda.interview.interview.post;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id " + id));
    }

    public Post createPost(Post post) {
        post.setId(null);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post post) {
        Post existing = getPostById(id);
        existing.setUserId(post.getUserId());
        existing.setTitle(post.getTitle());
        existing.setBody(post.getBody());
        return postRepository.save(existing);
    }

    public String deletePost(Long id) {
        if (!postRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id " + id);
        }
        return "Post with id " + id + " deleted successfully";
    }
}
