package com.example.demo.contoller;

import com.example.demo.dto.Task_Dto;
import com.example.demo.model.Task;
import com.example.demo.service.ProjectService;
import com.example.demo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //get
    @GetMapping("/admin/getAllTask")
    public List<Task> findAllTask() {
        return taskService.findAllTask();
    }

    //getById
    @GetMapping("/admin/getTask/{id}")
    public Task findTaskById(@PathVariable Integer id) {
        return taskService.findTaskById(id);
    }

    //Post
    @PostMapping("/admin/addTask")
    public String saveTask(@RequestBody Task_Dto task_Dto) {
        String result = taskService.SaveTask(task_Dto);
        if(result != null){
            return result;
        }
        return "project dose not exists";
    }

    //update
    @PutMapping("/admin/updateTask/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Integer id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        if(updatedTask != null){
            return ResponseEntity.ok(updatedTask);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //delete
    @DeleteMapping("/admin/deleteTask/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Integer id) {
        Task task = taskService.findTaskById(id);
        if(task != null){
            taskService.deleteTask(id);
            return ResponseEntity.ok("task deleted");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
        }
    }

    @GetMapping("/admin/getTaskByProjectId/{project_id}")
    public List<Task> findTaskByProjectId(@PathVariable Integer project_id) {
        return taskService.findAllByProjectId(project_id);
    }




}
