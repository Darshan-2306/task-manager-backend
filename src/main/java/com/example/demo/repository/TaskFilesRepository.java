package com.example.demo.repository;

import com.example.demo.model.TaskFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskFilesRepository extends JpaRepository<TaskFiles, Integer> {
    List<TaskFiles> findByTaskId(Integer taskId);
}
