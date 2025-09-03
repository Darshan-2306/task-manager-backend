package com.example.demo.repository;

import com.example.demo.model.Task_User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Task_User_Repository extends JpaRepository<Task_User, Integer> {

    List<Task_User> findByTaskId(int taskId);
    List<Task_User> findByUserId(int userId);
    boolean existsByTaskIdAndUserId(int taskId, int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Task_User u WHERE u.taskId = :taskId AND u.userId = :userId")
    Boolean deleteByUserIds(@Param("taskId") int taskId, @Param("userId") int userId);
}

