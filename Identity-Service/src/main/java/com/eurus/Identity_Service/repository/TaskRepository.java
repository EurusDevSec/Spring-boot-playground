package com.eurus.Identity_Service.repository;


import com.eurus.Identity_Service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface TaskRepository  extends JpaRepository<Task, String> {
    boolean existsTaskByTitle(String title);
    
}
