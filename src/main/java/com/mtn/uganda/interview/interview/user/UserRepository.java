package com.mtn.uganda.interview.interview.user;

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
 * In-memory store for {@link User} records seeded from the mock-data JSON file.
 */
@Repository
public class UserRepository {

    private final ObjectMapper objectMapper;

    @Value("${app.data.users-file}")
    private Resource usersFile;

    private final ConcurrentHashMap<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public UserRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void loadMockData() throws IOException {
        try (InputStream in = usersFile.getInputStream()) {
            List<User> users = objectMapper.readValue(in, new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {});
            for (User user : users) {
                store.put(user.getId(), user);
                sequence.accumulateAndGet(user.getId(), Math::max);
            }
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet());
        }
        store.put(user.getId(), user);
        return user;
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
