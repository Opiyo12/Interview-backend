package com.mtn.uganda.interview.interview.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id));
    }

    public User createUser(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existing = getUserById(id);
        existing.setName(user.getName());
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setWebsite(user.getWebsite());
        return userRepository.save(existing);
    }

    public String deleteUser(Long id) {
        if (!userRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id);
        }
        return "User with id " + id + " deleted successfully";
    }
}
