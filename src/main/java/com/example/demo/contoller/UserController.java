package com.example.demo.contoller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //get-admin
    @GetMapping("/admin/getAllUser")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //get by id-admin
    @GetMapping("/admin/getUser/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    //post by admin
    @PostMapping("/admin/newUser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // update
    @PutMapping("/admin/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        Optional UpdatedUser = userService.updateUser(id, user);
        if(UpdatedUser != null){
            return ResponseEntity.ok(UpdatedUser);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }



    //delete
    @DeleteMapping("/admin/deleteUser/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        User deletedUser = userService.getUserById(id);
        if(deletedUser != null){
            userService.deleteUser(id);
            return ResponseEntity.ok(deletedUser);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //get role by id using params
    @GetMapping("/admin/role")
    public String getUserRole(@RequestParam int id) {
        return userService.getUserRoleById(id);
    }

    //get user details based on role
    @GetMapping("/admin/byRole")
    public List<User> getUserDetails(@RequestParam String role) {
        return userService.getUserByRole(role);
    }

    @GetMapping("/my_details")
    public User getUserDetails() {
        try{
            return userService.getMy_Details();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/updateMy_Details")
    public ResponseEntity<?> updateUserDetails(@RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateMyDetails(userDetails); // call service
            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

}
