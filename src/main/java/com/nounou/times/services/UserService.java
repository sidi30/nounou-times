package com.nounou.times.services;

import com.nounou.times.model.User;
import com.nounou.times.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> getAllUsers() {
        System.out.println("UserService.getAllUsers");
        List<User> users = userRepository.listAll();
        return users;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(User user) {
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setRole(user.getRole());
            return existingUser;
        }
        return null;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
