package com.example.demo.service;

import com.example.demo.model.Project_User;
import com.example.demo.model.Task_User;
import com.example.demo.repository.Task_User_Repository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Task_User_Service {
    private final Task_User_Repository taskUserRepository;
    private final TaskService taskService;
    private final UserService userService;
    public Task_User_Service(Task_User_Repository taskUserRepository, TaskService taskService, UserService userService) {
        this.taskUserRepository = taskUserRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public List<Integer> FindByTaskId(Integer taskId) {
        return taskUserRepository.findByTaskId(taskId)
                .stream()
                .map(Task_User::getUserId)
                .toList();
    }

    public List<Integer> FindByUserId(Integer userId) {
        return taskUserRepository.findByUserId(userId)
                .stream()
                .map(Task_User::getTaskId)
                .toList();
    }

    public String AddTaskAndUser(Integer taskId,Integer userId) {

        if(!taskService.existsById(taskId))
        {
            return "task id not exits";
        }
        if(!userService.existsById(userId))
        {
            return "user id not exits";
        }
        if(taskUserRepository.existsByTaskIdAndUserId(taskId, userId)){
            return "already exists";
        }

        Task_User taskUser = new Task_User();
        taskUser.setTaskId(taskId);
        taskUser.setUserId(userId);
        taskUserRepository.save(taskUser);
        return "success";
    }

    public String getTasksForLoggedInUser() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        if (auth == null || auth.getName() == null) {
            throw new Exception("User not authenticated");
        }

        String email = auth.getName();
        System.out.println("Email: " + email);

        int userId = userService.getIdByEmail(email);
        System.out.println("UserId: " + userId);

        List<Task_User> taskUsers = taskUserRepository.findByUserId(userId);
        System.out.println("Tasks found: " + taskUsers.size()); // debug

        String taskIds = taskUsers.stream()
                .map(Task_User::getTaskId)
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
        return taskIds;
//        if(!taskIds.equals("")) {
//            return "Task assigned to user id: " + userId + " is task id " + taskIds;
//        }
//        else {
//            return "No task is assigned to user id:"+userId;
//        }

    }




}


