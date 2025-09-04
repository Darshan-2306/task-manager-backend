package com.example.demo.repository;

import com.example.demo.model.Project_User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Project_User_repository extends JpaRepository<Project_User,Integer> {
    List<Project_User> findByProjectId(int projectId);
    List<Project_User> findByUserId(int userId);
    boolean existsByUserIdAndProjectId(int userId, int projectId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Project_User u WHERE u.projectId = :projectId AND u.userId = :userId")
    int deleteByUserIds(@Param("projectId") int projectId, @Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("Delete FROM Project_User u WHERE u.userId = :userId")
    int deleteByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("Delete FROM Project_User u WHERE u.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") int projectId);

}
