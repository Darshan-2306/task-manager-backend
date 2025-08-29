package com.example.demo.contoller;

import com.example.demo.dto.Email_Dto;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @Autowired
    public UserService userService;

    @PostMapping( "/send")
    public void sendEmail(@RequestBody Email_Dto email_Dto){
        String email = userService.getEmailById(email_Dto.getToId());
        emailService.sendSimpleMail(email,
                email_Dto.getSubject(),
                email_Dto.getBody());
    }

}
