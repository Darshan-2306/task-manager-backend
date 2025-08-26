package com.example.demo.service;

import com.example.demo.dto.Task_Dto;
import com.example.demo.model.Task;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<Task> findAllTask() {
        return taskRepository.findAll();
    }

    public Task findTaskById(Integer task_id) {
        return taskRepository.findById(task_id).orElse(null);
    }

    public String SaveTask(@RequestBody Task_Dto task_Dto) {
        if(projectRepository.existsById(task_Dto.getProjectId())) {
            Task task = new Task();

            task.setTaskId(task_Dto.getTaskId());
            task.setTaskName(task_Dto.getTaskName());
            task.setProjectId(task_Dto.getProjectId());
            task.setTaskDescription(task_Dto.getTaskDescription());
            taskRepository.save(task);
            return "success";
        }
        else{
            return null;
        }
    }

    public Task updateTask(int task_id,@RequestBody Task taskdetails){
        return taskRepository.findById(task_id)
                .map(task ->{
                    task.setTaskName(taskdetails.getTaskName());
                    task.setProjectId(taskdetails.getProjectId());
                    task.setTaskDescription(taskdetails.getTaskDescription());
                    return taskRepository.save(task);
                })
                .orElse(null);
    }

    public void deleteTask(Integer task_id) {
        taskRepository.deleteById(task_id);
    }

    public List<Task> findAllById(List<Integer> task_id) {
        return taskRepository.findAllById(task_id);

    }

    public boolean existsById(Integer task_id) {
        return taskRepository.existsById(task_id);
    }

    public List<Task> findAllByProjectId(Integer project_id) {
        return taskRepository.findAllByProjectId(project_id);
    }



}
