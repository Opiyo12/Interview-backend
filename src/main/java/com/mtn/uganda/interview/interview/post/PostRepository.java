package com.mtn.uganda.interview.interview.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory store for {@link Post} records seeded from the mock-data JSON file.
 */
@Repository
public class PostRepository {

    private final ObjectMapper objectMapper;

    @Value("${app.data.posts-file}")
    private Resource postsFile;

    private final ConcurrentHashMap<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public PostRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void loadMockData() throws IOException {
        try (InputStream in = postsFile.getInputStream()) {
            List<Post> posts = objectMapper.readValue(in, new TypeReference<List<Post>>() {});
            for (Post post : posts) {
                store.put(post.getId(), post);
                sequence.accumulateAndGet(post.getId(), Math::max);
            }
        }
    }

    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Post> findByUserId(Long userId) {
        return store.values().stream()
                .filter(post -> userId.equals(post.getUserId()))
                .toList();
    }

    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(sequence.incrementAndGet());
        }
        store.put(post.getId(), post);
        return post;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
