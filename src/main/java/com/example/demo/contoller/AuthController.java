package com.example.demo.contoller;

import com.example.demo.model.JwtUtil;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            if(newUser == null){
                return ResponseEntity.ok("User already exists");
            }
            return ResponseEntity.ok("User registered successfully with ID: " + newUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginRequest, HttpServletResponse response) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            User user = userService.authenticate(email, password);

            String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());

            ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
                    .httpOnly(true)
                    .secure(false) // set true in production (HTTPS)
                    .path("/")
                    .maxAge(15 * 60)
                    .sameSite("Strict")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: " + e.getMessage());
        }
    }


}
