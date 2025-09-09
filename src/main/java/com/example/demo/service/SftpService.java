package com.example.demo.service;

import com.example.demo.Config.SftpConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class SftpService {

    private final SftpConfig sftpConfig;
    private final SftpRemoteFileTemplate sftpTemplate;


    @PostConstruct
    public void testConnection() {
        try {
            sftpTemplate.execute(session -> {
                DirEntry[] files = session.list(sftpConfig.getRemoteDir());

                if (files == null || files.length == 0) {
                    System.out.println("Directory is empty");
                } else {
                    System.out.println("Files in " + sftpConfig.getRemoteDir() + ":");
                    for (DirEntry f : files) {
                        System.out.println(" - " + f.getFilename());
                    }
                }
                return null;
            });
        } catch (Exception e) {
            System.err.println(" SFTP connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void upload(InputStream inputStream, String remoteFileName) throws Exception {
        sftpTemplate.execute(session -> {
            String remotePath = sftpConfig.getRemoteDir() + "/" + remoteFileName;
            // Send the inputStream directly to remote path
            session.write(inputStream, remotePath);
            System.out.println("Uploaded → " + remotePath);
            return null;
        });
    }


    public List<String> listFiles() throws Exception {
        return sftpTemplate.execute(session -> {
            DirEntry[] files = session.list(sftpConfig.getRemoteDir());

            if (files == null || files.length == 0) {
                return List.of(); // empty list
            } else {
                // Convert DirEntry array to List<String> of filenames
                return Arrays.stream(files)
                        .map(DirEntry::getFilename)
                        .toList();
            }
        });
    }


    public void deleteFile(String remoteFileName) throws Exception {
        sftpTemplate.execute(session -> {
            String remotePath = sftpConfig.getRemoteDir() + "/" + remoteFileName;
            session.remove(remotePath);
            System.out.println("Deleted file: " + remotePath);
            return null;
        });
    }

    public void downloadFile(String remoteFileName, String localFilePath) throws Exception {
        boolean exits = RemoteFileExists(remoteFileName);
        if (!exits) {
            System.out.println("file does not exist");
            throw new Exception("Remote file " + remoteFileName + " does not exist");
        }
        sftpTemplate.execute(session -> {
            String remotePath = sftpConfig.getRemoteDir() + "/" + remoteFileName;
            try(FileOutputStream fos = new FileOutputStream(localFilePath)) {
                session.read(remotePath,fos);
                System.out.println("Downloaded"+remotePath+" -> " +localFilePath);
            }
            return null;
        });
    }

    public boolean RemoteFileExists(String remoteFileName) throws Exception {
        return sftpTemplate.execute(session -> {
            String baseDir = sftpConfig.getRemoteDir().replaceAll("/$", "");
            String remotePath = baseDir + "/" + remoteFileName;
            try{
                session.exists(remotePath);
                return true;
            }
            catch (Exception e){
                return false;
            }
        });
    }

    public void downloadAndZip(List<String> remoteFileNames, OutputStream outputStream) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (String remoteFileName : remoteFileNames) {
                sftpTemplate.execute(session -> {
                    String remotePath = sftpConfig.getRemoteDir() + "/" + remoteFileName;
                    try (InputStream is = session.readRaw(remotePath)) {
                        zos.putNextEntry(new ZipEntry(remoteFileName));
                        byte[] buffer = new byte[1024 * 1024]; // 1MB chunks
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            zos.write(buffer, 0, bytesRead);
                        }

                        zos.closeEntry();
                        System.out.println("Added to zip: " + remoteFileName);
                    }
                    return null;
                });
            }
        }
    }


}
