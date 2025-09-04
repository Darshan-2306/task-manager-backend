package com.example.demo.repository;

import com.example.demo.model.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // Finds tasks by projectId (returns Task objects)
    List<Task> findAllByProjectId(Integer projectId);

    // Delete all tasks belonging to a project
    @Modifying
    @Transactional
    @Query("DELETE FROM Task u WHERE u.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") Integer projectId);

    // Get only taskIds of tasks that belong to a project
    @Query("SELECT u.taskId FROM Task u WHERE u.projectId = :projectId")
    List<Integer> findTaskIdsByProjectId(@Param("projectId") Integer projectId);
}

