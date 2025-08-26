package com.example.demo.repository;

import com.example.demo.model.Project_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Project_User_repository extends JpaRepository<Project_User,Integer> {
    List<Project_User> findByProjectId(int projectId);
    List<Project_User> findByUserId(int userId);
}
