package com.example.demo.Config;

import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

@Configuration
public class SftpIntegerationConfig {

    private final SftpConfig sftpConfig;

    public SftpIntegerationConfig(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    @Bean
    public SessionFactory<DirEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(sftpConfig.getHost());
        factory.setPort(sftpConfig.getPort());
        factory.setUser(sftpConfig.getUsername());
        factory.setPassword(sftpConfig.getPassword());
        factory.setAllowUnknownKeys(true);
        return factory;
    }

    @Bean
    public SftpRemoteFileTemplate sftpTemplate(SessionFactory<DirEntry> factory) {
        return new SftpRemoteFileTemplate(factory);
    }
}
