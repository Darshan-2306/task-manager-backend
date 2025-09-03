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

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestBody SftpDownload_Dto request) {
        try{
            sftpService.downloadFile(request.getRemoteFileName(),request.getLocalPath());
            return ResponseEntity.ok("File downloaded successfully: " + request.getLocalPath());
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to download file: " + e.getMessage());
        }
    }

    @PostMapping("/download-zip")
    public ResponseEntity<byte[]> downloadZipFile(@RequestBody List<String> remoteFilename) {
        try{
            byte[] zipBytes = sftpService.downloadAndZip(remoteFilename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        }
        catch (Exception e){
            return  ResponseEntity.internalServerError()
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }


}
