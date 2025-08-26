package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Task_Dto {
    private int taskId;
    private String taskName;
    private int projectId;
    private String taskDescription;


}
