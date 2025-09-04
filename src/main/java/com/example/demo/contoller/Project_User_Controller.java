package com.example.demo.contoller;

import com.example.demo.dto.Project_User_Dto;
import com.example.demo.model.Project;
import com.example.demo.model.Project_User;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.Project_User_repository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.Project_User_Service;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project_user")
public class Project_User_Controller {
    private final Project_User_Service project_User_Service;
    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    @Autowired
    public Project_User_repository projectUserRepository;
    public Project_User_Controller(Project_User_Service project_User_Service, UserService userService, ProjectService projectService , ProjectRepository projectRepository) {
        this.project_User_Service = project_User_Service;
        this.userService = userService;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/admin/UserDetail/{projectId}")
    public List<User> findByProjectId(@PathVariable int projectId) {
        List<Integer> UserIds = project_User_Service.findByProjectId(projectId);
        return userService.findAllById(UserIds);
    }

    @GetMapping("/admin/ProjectDetail/{userId}")
    public List<Project> findByUserId(@PathVariable int userId) {
        List<Integer> ProjectIds = project_User_Service.findByUserId(userId);
        return projectService.findByProjectId(ProjectIds);
    }

    @PostMapping("/admin/add")
    public String addProjectAndUser(@RequestBody Project_User_Dto project_User_Dto) {
        return project_User_Service.AddProjectAndUser(project_User_Dto);
    }

    @GetMapping("/my_projects")
    public List<Project> getMyProjects() {
        try{
            String projectIds = project_User_Service.getProjectForLoggedInUser();
            if (projectIds == null || projectIds.isEmpty()) {
                return List.of(); // return empty list
            }
            List<Integer> ids = Arrays.stream(projectIds.split(" "))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());


            return projectRepository.findAllById(ids);
        } catch (Exception e)
        {
            e.printStackTrace(); // log the real exception
            return null;
        }
    }

    @DeleteMapping("/admin/delete")
    public String deleteProjectAndUser(@RequestBody Project_User_Dto project_User_Dto) {
        if(projectUserRepository.deleteByUserIds(
                project_User_Dto.getProjectId(),
                project_User_Dto.getUserId()) > 0)
        {
            return "success";
        } else {
            return "fail";
        }
    }

    @DeleteMapping("/admin/deleteByUser")
    public String deleteByUser(@RequestBody Project_User_Dto project_User_Dto) {
        if(projectUserRepository.deleteByUserId(project_User_Dto.getUserId()) > 0)
        {
            return "success";
        }
        else{
            return "fail";
        }
    }

    @DeleteMapping("/admin/deleteByProject")
    public String deleteByProject(@RequestBody Project_User_Dto project_User_Dto) {
        if(projectUserRepository.deleteByProjectId(project_User_Dto.getProjectId()) > 0)
        {
            return "success";
        }
        else{
            return "fail";
        }
    }
}
