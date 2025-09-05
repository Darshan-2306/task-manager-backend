package com.example.demo.contoller;


import com.example.demo.dto.ZipEmailRequest_Dto;
import com.example.demo.service.EmailService;
import com.example.demo.service.SftpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("sftp/zip")
public class ZipEmailController {

    private Map<String, List<String>> tokenStore = new ConcurrentHashMap<>();

    @Autowired
    private SftpService sftpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-link")
    public ResponseEntity<String> sendZipDownloadLink(@RequestBody ZipEmailRequest_Dto request) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, request.getRemoteFilenames());

        String downloadUrl = "https://10ab67ec8bf4.ngrok-free.app/sftp/zip/download/" + token;

        emailService.sendSimpleMail(
                request.getEmail(),
                "Your ZIP Download Link",
                "Click here to download your files: " + downloadUrl
        );

        return ResponseEntity.ok("Download link sent to " + request.getEmail());
    }

    // 2️⃣ API: when user clicks the link
    @GetMapping("/download/{token}")
    public ResponseEntity<StreamingResponseBody> downloadZip(@PathVariable String token) {
        List<String> files = tokenStore.get(token);
        if (files == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(outputStream -> {
                    try {
                        sftpService.downloadAndZip(files, outputStream);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to stream zip", e);
                    }
                });

    }


}
