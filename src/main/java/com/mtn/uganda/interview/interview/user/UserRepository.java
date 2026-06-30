package com.mtn.uganda.interview.interview.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserRepository {

    private List<User> users = new ArrayList<>();

    // read the users from the mock data json file
    public List<User> loadFromMockData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = new ClassPathResource("mock-data/users.json").getInputStream();
            User[] data = mapper.readValue(input, User[].class);
            users = new ArrayList<>(Arrays.asList(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> findAll() {
        // load the data first if it is not loaded yet
        if (users.isEmpty()) {
            loadFromMockData();
        }
        return users;
    }

    public User findById(Long id) {
        // make sure the data is loaded
        if (users.isEmpty()) {
            loadFromMockData();
        }
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
