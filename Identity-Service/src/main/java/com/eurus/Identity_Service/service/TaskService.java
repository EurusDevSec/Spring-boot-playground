package com.eurus.Identity_Service.service;


import com.eurus.Identity_Service.dto.request.TaskCreationRequest;
import com.eurus.Identity_Service.dto.request.TaskDeleteRequest;
import com.eurus.Identity_Service.dto.request.TaskUpdateRequest;
import com.eurus.Identity_Service.entity.Task;
import com.eurus.Identity_Service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service


public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    //Create Task
    public Task createTask(TaskCreationRequest request){
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        return taskRepository.save(task);
    }

    //Get Tasks
    public List<Task> getTasks(){
        return taskRepository.findAll();
    }

    //Get Task by id
    public Task getTask(String id){
        return taskRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Task not found"));
    }

    // updateTask
    public Task updateTask(String taskId, TaskUpdateRequest request){
        // tim kiem task theo Id bang getTask
        Task task = getTask(taskId);

        // Cap nhat cac truong co trong TaskUpdateRequest
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());


        // Luu lai bang taskRepository
        return taskRepository.save(task);
    }

    //deleteTask
    public void deleteTask(String taskId, TaskDeleteRequest request){
        taskRepository.deleteById(taskId);
    }


}
