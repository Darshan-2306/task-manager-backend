package com.example.demo.contoller;

import com.example.demo.Config.SftpConfig;
import com.example.demo.dto.SftpDownload_Dto;
import com.example.demo.model.TaskFiles;
import com.example.demo.repository.TaskFilesRepository;
import com.example.demo.service.SftpService;
import lombok.RequiredArgsConstructor;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sftp")
public class SftpController {

    private final SftpService sftpService;

    @Autowired
    private SftpConfig sftpConfig;

    @Autowired
    private TaskFilesRepository taskFilesRepository;

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

//    @PostMapping("/download")
//    public ResponseEntity<String> downloadFile(@RequestBody SftpDownload_Dto request) {
//        try{
//            long start = System.currentTimeMillis();
//            sftpService.downloadFile(request.getRemoteFileName(),request.getLocalPath());
//            long end = System.currentTimeMillis();
//            long duration = end - start;
//            System.out.println("download took : " + (duration / 1000.0) + " seconds");
//            return ResponseEntity.ok("File downloaded successfully: " + request.getLocalPath());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Failed to download file: " + e.getMessage());
//        }
//    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String remoteFileName) {
        try {
            // Download the file temporarily from SFTP to server temp folder
            String userHome = System.getProperty("user.home");
            String tempPath = userHome + "/Downloads/" + remoteFileName;

            // Call your sftpService to download from SFTP into temp file
            sftpService.downloadFile(remoteFileName, tempPath);

            // Wrap file into Resource
            File file = new File(tempPath);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
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


    @PostMapping("/upload-multiple")
    public ResponseEntity<String> uploadMultipleFiles(
            @RequestParam("taskId") Integer taskId,     // add taskId from frontend
            @RequestParam("files") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                String remoteFileName = file.getOriginalFilename();
                String remotePath = sftpConfig.getRemoteDir() + "/" + remoteFileName;
                try (InputStream inputStream = file.getInputStream()) {
                    long start = System.currentTimeMillis();
                    sftpService.upload(inputStream, remoteFileName);
                    long end = System.currentTimeMillis();
                    System.out.println("Uploaded: " + remoteFileName +
                            " in " + (end - start) / 1000.0 + " seconds");
                }
                // store metadata in DB
                TaskFiles taskFile = new TaskFiles();
                taskFile.setTaskId(taskId);
                taskFile.setFileName(remoteFileName);
                taskFile.setFilePath(remotePath);
                taskFilesRepository.save(taskFile);
            }

            return ResponseEntity.ok(files.length + " files uploaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload files: " + e.getMessage());
        }
    }

    @GetMapping("/getAttachedFiles")
    public List<TaskFiles> findByTaskId(@RequestParam Integer taskId) {
        return taskFilesRepository.findByTaskId(taskId);
    }






}
