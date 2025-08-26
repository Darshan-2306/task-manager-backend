package com.example.demo.service;

import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAllProjects(){
        return projectRepository.findAll();
    }

    public Project findProjectById(int project_id){
        return projectRepository.findById(project_id).orElse(null);
    }

    public Project saveProject(Project project){
        return projectRepository.save(project);
    }

    public Project updateProject(int project_id,Project projectDetails){
        return projectRepository.findById(project_id)
                .map(project ->{
                    project.setProject_name(projectDetails.getProject_name());
                    project.setProject_description(projectDetails.getProject_description());
                    return projectRepository.save(project);
                } ).orElse(null);
    }

    public boolean deleteProject(int project_id){
        if(projectRepository.existsById(project_id)) {
            projectRepository.deleteById(project_id);
            return true;
        }
        return false;
    }

    public List<Project> findByProjectId(List<Integer> projectIds){
        return projectRepository.findAllById(projectIds);
    }

    public boolean existsByProjectId(int projectId){
        return projectRepository.existsById(projectId);
    }



}
