package com.example.demo.service;

import com.example.demo.dto.Project_User_Dto;
import com.example.demo.model.Project_User;
import com.example.demo.model.Task_User;
import com.example.demo.repository.Project_User_repository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Project_User_Service {
    private final Project_User_repository project_User_repository;
    private final ProjectService projectService;
    private final UserService userService;
    public Project_User_Service(Project_User_repository project_User_repository, ProjectService projectService, UserService userService) {
        this.project_User_repository = project_User_repository;
        this.projectService = projectService;
        this.userService = userService;
    }

    public List<Integer> findByProjectId(Integer project_id){
        return project_User_repository.findByProjectId(project_id).stream().map(Project_User::getUserId).toList();
    }

    public List<Integer> findByUserId(Integer user_id){
        return project_User_repository.findByUserId(user_id).stream()
                .map(Project_User::getProjectId).toList();
    }

    public String AddProjectAndUser(@RequestBody Project_User_Dto project_User_Dto){
        if(!projectService.existsByProjectId(project_User_Dto.getProjectId())){
            return "project id not exits";
        }
        if(!userService.existsById(project_User_Dto.getUserId())){
            return "user id not exits";
        }
        if(project_User_repository.existsById(project_User_Dto.getUserId()) && project_User_repository.existsById(project_User_Dto.getProjectId())){
            return "already exists";
        }
        Project_User project_User = new Project_User();
        project_User.setProjectId(project_User_Dto.getProjectId());
        project_User.setUserId(project_User_Dto.getUserId());
        project_User_repository.save(project_User);
        return "success";
    }

    public String getProjectForLoggedInUser() throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        if (auth == null || auth.getName() == null) {
           throw new Exception("User not authenticated");
        }
        String email = auth.getName();
        System.out.println("Email: " + email);
        int userId = userService.getIdByEmail(email);
        System.out.println("UserId: " + userId);

        List<Project_User>  projectUsers = project_User_repository.findByUserId(userId);
        System.out.println("Tasks found: " + projectUsers.size());


        String projectIds = projectUsers.stream().map(Project_User::getProjectId).map(String::valueOf).collect(Collectors.joining(" "));

        return projectIds;
//        if(!projectIds.equals("")) {
//            return projectIds;
//        }
//        else {
//            return "No project is assigned to user id:"+userId;
//        }
    }


}
