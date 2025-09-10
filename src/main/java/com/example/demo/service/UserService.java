package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.*;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        if(user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

        }
        return userRepository.save(user);
    }

    public Optional<User> updateUser(int id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userDetails.getName() != null && !userDetails.getName().isEmpty()) {
                        user.setName(userDetails.getName());
                    }

                    if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
                        user.setEmail(userDetails.getEmail());
                    }

                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }

                    if (userDetails.getRole() != null && !userDetails.getRole().isEmpty()) {
                        user.setRole(userDetails.getRole());
                    }

                    return userRepository.save(user);
                });
    }


    public void deleteUser(int id) {
            userRepository.deleteById(id);
    }

    public String getUserRoleById(int id) {
        return userRepository.findById(id)
                .map(user -> user.getRole())
                .orElse("User not found");
    }

    public List<User> getUserByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAllById(List<Integer> userIds) {
        return userRepository.findAllById(userIds);
    }

    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }



    //registeration
    public User registerUser(User user){
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole("User");
        Optional<User> user1 = userRepository.findByEmail(user.getEmail());
        if(user1.isPresent()){
            return null;
        }
        return userRepository.save(user);
    }

//    authentication
    public User authenticate(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
        {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword()))
        {
            throw new RuntimeException("Wrong password");
        }
        return user;
    }

    public int getIdByEmail(String email) throws Exception {
        Integer userId = userRepository.findIdByEmail(email);
        if (userId == null) throw new Exception("User not found");
        return userId;
    }

    public User getMy_Details() throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        if (auth == null || auth.getName() == null) {
            throw new Exception("User not authenticated");
        }

        String email = auth.getName();
        System.out.println("Email: " + email);
        return userRepository.findUserByEmail(email);
    }

    public User updateMyDetails(User userDetails) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new Exception("User not authenticated");
        }

        User user = userRepository.findUserByEmail(auth.getName());
        if (user == null) {
            throw new Exception("User not found");
        }
        if (userDetails.getName() != null && !userDetails.getName().isEmpty()) {
            user.setName(userDetails.getName());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public String getEmailById(int id) {
        return userRepository.findEmailById(id);
    }

    public User loginWithGoogle(String googleId, String email, String name) {
        //  Check by Google ID
        Optional<User> optionalUser = userRepository.findByGoogleId(googleId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        // same email exists
        Optional<User> existingEmailUser = userRepository.findByEmail(email);
        if (existingEmailUser.isPresent()) {
            User user = existingEmailUser.get();
            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");
            return userRepository.save(user);
        }

        //  new
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setGoogleId(googleId);
        newUser.setAuthProvider("GOOGLE");
        newUser.setRole("User");
        newUser.setPassword(null);
        return userRepository.save(newUser);
    }







}
