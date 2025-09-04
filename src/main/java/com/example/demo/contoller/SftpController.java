package com.example.demo.contoller;

import com.example.demo.dto.SftpDownload_Dto;
import com.example.demo.service.SftpService;
import lombok.RequiredArgsConstructor;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sftp")
public class SftpController {

    private final SftpService sftpService;

    @GetMapping("/list")
    public List<String> listFiles() throws Exception {
        return sftpService.listFiles();
    }



    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "remoteName", required = false) String remoteName
    ) {
        try {
            String remoteFileName = (remoteName != null && !remoteName.isEmpty())
                    ? remoteName
                    : file.getOriginalFilename();

            try (InputStream inputStream = file.getInputStream()) {
                long start = System.currentTimeMillis();
                sftpService.upload(inputStream, remoteFileName);
                long end = System.currentTimeMillis();
                long duration = end - start;
                System.out.println("Upload took: " + (duration / 1000.0) + " seconds");
            }

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

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestBody SftpDownload_Dto request) {
        try{
            long start = System.currentTimeMillis();
            sftpService.downloadFile(request.getRemoteFileName(),request.getLocalPath());
            long end = System.currentTimeMillis();
            long duration = end - start;
            System.out.println("download took : " + (duration / 1000.0) + " seconds");
            return ResponseEntity.ok("File downloaded successfully: " + request.getLocalPath());
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to download file: " + e.getMessage());
        }
    }

    @PostMapping("/download-zip")
    public ResponseEntity<StreamingResponseBody> downloadZipFile(@RequestBody List<String> remoteFilenames) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(outputStream -> {
                    try {
                        long start = System.currentTimeMillis();
                        sftpService.downloadAndZip(remoteFilenames, outputStream);
                        long end = System.currentTimeMillis();
                        System.out.println("Download as zip took: " + (end - start) / 1000.0 + " seconds");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to stream zip: " + e.getMessage(), e);
                    }
                });
    }




}
