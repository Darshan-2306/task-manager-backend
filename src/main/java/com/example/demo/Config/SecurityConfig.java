package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login","/user/my_details","/api/file/upload","/sftp/list","/sftp/upload","/sftp/delete"
                        ,"/sftp/download","/sftp/download-zip","sftp/zip/send-link","sftp/zip/download/{token}","/sftp/upload-multiple","/sftp/getAttachedFiles").permitAll()

                        .requestMatchers("/user/admin/getAllUser","/user/admin/getUser/{id}",
                                "/user/admin/newUser","/user/admin/updateUser/{id}","/user/admin/deleteUser/{id}","/user/admin/role",
                                "/user/admin/byRole").hasRole("Admin")
                        .requestMatchers("/task/admin/getAllTask","/task/admin/getTask/{id}","/task/admin/addTask"
                                ,"/task/admin/updateTask/{id}","/task/admin/deleteTask/{id}",
                                "/task/admin/getTaskByProjectId/{project_id}").hasRole("Admin")
                        .requestMatchers("/project/admin/getAllProject","/project/admin/getProjectById/{id}","/project/admin/addNewProject",
                                "/project/admin/updateProject/{id}","/project/admin/deleteProject/{id}").hasRole("Admin")
                        .requestMatchers("/project_user/admin/UserDetail/{projectId}","/project_user/admin/ProjectDetail/{userId}",
                                "/project_user/admin/add","/project_user/admin/delete","/project_user/admin/deleteByUser","project_user/admin/deleteByProject").hasRole("Admin")
                        .requestMatchers("/task_User/admin/UserDetails/{taskId}","/task_User/admin/TaskDetails/{taskId}",
                                "/task_User/admin/add","/task_User/admin/deleteByUser","/task_User/admin/deleteByProj","/task_User/admin/deleteByTask","/task_User/admin/deleteByUserandTask").hasRole("Admin")
                        .requestMatchers("/api/email/send").hasRole("Admin")


                        .requestMatchers("/updateMy_Details").hasAnyRole("Admin", "User")
                        .requestMatchers("/task_User/my_tasks").hasAnyRole("Admin", "User")
                        .requestMatchers("/project_user/my_projects").hasAnyRole("Admin", "User")
                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}