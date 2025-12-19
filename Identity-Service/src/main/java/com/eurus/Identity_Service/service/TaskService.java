package com.eurus.Identity_Service.service;


import com.eurus.Identity_Service.dto.request.TaskCreationRequest;
import com.eurus.Identity_Service.entity.Task;
import com.eurus.Identity_Service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service


public class TaskService {

    @Autowired
    private TaskRepository taskRepository;


    public Task createTask(TaskCreationRequest request){
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        return taskRepository.save(task);
    }



}
