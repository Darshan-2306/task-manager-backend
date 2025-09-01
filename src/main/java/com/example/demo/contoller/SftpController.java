package com.example.demo.contoller;

import com.example.demo.service.SftpService;
import lombok.RequiredArgsConstructor;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sftp")
public class SftpController {

    private final SftpService sftpService;

    @GetMapping("/list")
    public List<String> listFiles() throws Exception {
        return sftpService.listFiles(); // Spring Boot automatically converts List<String> to JSON
    }



    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "remoteName", required = false) String remoteName
    ) {
        try {
            // Determine remote file name
            String remoteFileName = (remoteName != null && !remoteName.isEmpty())
                    ? remoteName
                    : file.getOriginalFilename();

            // Save multipart file temporarily
            File tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }


            sftpService.upload(tempFile.getAbsolutePath(), remoteFileName);
            tempFile.delete();

            return ResponseEntity.ok("File uploaded successfully: " + remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            sftpService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
        }
    }
}
