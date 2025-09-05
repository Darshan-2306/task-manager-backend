package com.example.demo.contoller;

import com.example.demo.dto.Project_User_Dto;
import com.example.demo.dto.Task_Dto;
import com.example.demo.dto.Task_User_Dto;
import com.example.demo.model.Task;
import com.example.demo.model.Task_User;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.Task_User_Repository;
import com.example.demo.service.TaskService;
import com.example.demo.service.Task_User_Service;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task_User")
public class Task_User_Controller {
    private final Task_User_Service task_User_Service;
    private final UserService userService;
    public final TaskService taskService;

    @Autowired
    public TaskRepository taskRepository;
    @Autowired
    public Task_User_Repository  task_User_Repository;


    public Task_User_Controller(Task_User_Service task_User_Service,UserService userService,TaskService taskService) {
        this.task_User_Service = task_User_Service;
        this.userService = userService;
        this.taskService = taskService;

    }

    @GetMapping("/admin/UserDetails/{taskId}")
    public List<User> findByTaskId(@PathVariable int taskId) {
        List<Integer> UserIds = task_User_Service.FindByTaskId(taskId);
        return userService.findAllById(UserIds);
    }

    @GetMapping("/admin/TaskDetails/{userId}")
    public List<Task> findByUserId(@PathVariable int userId) {
        List<Integer> TaskIds = task_User_Service.FindByUserId(userId);
        return taskService.findAllById(TaskIds);
    }

    @PostMapping("/admin/add")
    public String addTaskAndUser(@RequestBody Task_User_Dto task_User_Dto) {
        return task_User_Service.AddTaskAndUser(task_User_Dto.getTaskId(),task_User_Dto.getUserId());
    }

    @GetMapping("/my_tasks")
    public List<Task> getMyTasks() {
        try{
            String taskIds = task_User_Service.getTasksForLoggedInUser();
            if ( taskIds == null || taskIds.isEmpty()) {
                return List.of(); // return empty list
            }
            List<Integer> ids = Arrays.stream(taskIds.split(" "))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            return taskService.findAllById(ids);
        } catch (Exception e)
        {
            e.printStackTrace(); // log the real exception
            return null;
        }
    }

    @DeleteMapping("/admin/delete")
    public String deleteTaskAndUser(@RequestBody Task_User_Dto task_User_Dto) {
        if(task_User_Repository.deleteByUserIds(task_User_Dto.getTaskId(),task_User_Dto.getUserId())){
            return "success";
        }
        else{
            return "fail";
        }
    }


    @DeleteMapping("/admin/deleteByUser")
    public String deleteByUser(@RequestBody Project_User_Dto project_User_Dto) {
        if(task_User_Repository.deleteByUserId(project_User_Dto.getUserId()) > 0)
        {
            return "success";
        }
        else{
            return "fail";
        }
    }


    @DeleteMapping("/admin/deleteByTask")
    public String deleteByTask(@RequestBody Task_User_Dto task_User_Dto) {
        if(task_User_Repository.deleteByTask(task_User_Dto.getTaskId()) > 0){
            return "success";
        }
        else{
            return "fail";
        }
    }

    @DeleteMapping("/admin/deleteByProj")
    public String deleteByProj(@RequestBody Project_User_Dto project_User_Dto) {
        List<Integer> Ids = taskRepository.findTaskIdsByProjectId(project_User_Dto.getProjectId());
        try {
            for (Integer id : Ids){
                task_User_Repository.deleteByTask(id);
            }

            for (Integer id : Ids) {
                taskRepository.deleteByProjectId(project_User_Dto.getProjectId());
            }

            return "success";
        }
        catch (Exception e) {
            return "fail";
        }

    }

    @DeleteMapping("/admin/deleteByUserandTask")
    public String deleteByUserAndTask(@RequestBody Map<String,Integer> UserTask) {

        if((task_User_Repository.deleteByTaskAndUserId(UserTask.get("taskId"),UserTask.get("userId")))>0){
            return "success";
        }
        else{
            return "fail";
        }
    }




}
