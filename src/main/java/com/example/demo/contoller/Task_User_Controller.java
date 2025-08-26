package com.example.demo.contoller;

import com.example.demo.dto.Task_User_Dto;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.Task_User_Repository;
import com.example.demo.service.TaskService;
import com.example.demo.service.Task_User_Service;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task_User")
public class Task_User_Controller {
    private final Task_User_Service task_User_Service;
    private final UserService userService;
    public final TaskService taskService;

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


}
