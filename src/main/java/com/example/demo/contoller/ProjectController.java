package com.example.demo.contoller;

import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.*;


@RestController
@RequestMapping("/project")

public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //get
    @GetMapping("/admin/getAllProject")
    public List<Project> getProjects() {
        return projectService.findAllProjects();
    }

    //get by id
    @GetMapping("/admin/getProjectById/{id}")
    public Project getProjectById(@PathVariable int id) {
        return projectService.findProjectById(id);
    }

    //post
    @PostMapping("/admin/addNewProject")
    public Project saveProject(@RequestBody Project project){
        return projectService.saveProject(project);
    }

    //update
    @PutMapping("/admin/updateProject/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable int id, @RequestBody Project projectDetails){
        Project UpdatedProject = projectService.updateProject(id, projectDetails);
        if(UpdatedProject != null){
            return  ResponseEntity.ok(UpdatedProject);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //delete
    @DeleteMapping("/admin/deleteProject/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable int id){
        boolean deleteproject =  projectService.deleteProject(id);
        if(deleteproject){

            return ok("successfully deleted");
        }
        else{
            return status(HttpStatus.NOT_FOUND)
                    .body("Project not found");
        }
    }



}
