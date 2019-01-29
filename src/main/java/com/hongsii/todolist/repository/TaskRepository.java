package com.hongsii.todolist.repository;

import com.hongsii.todolist.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
