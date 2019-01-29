package com.hongsii.todolist.service;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.exception.NotFoundTaskException;
import com.hongsii.todolist.repository.TaskRepository;
import com.hongsii.todolist.service.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;

	@Transactional
	public TaskDto.Response create(TaskDto.Create request) {
		Task target = request.toEntity();
		Task created = taskRepository.save(target);
		createRelatedTasks(request, created);
		return TaskDto.Response.of(created);
	}

	private void createRelatedTasks(TaskDto.Create request, Task task) {
		for (Long relatedTaskId : request.getRelatedTaskIds()) {
			Task savedRelatedTask = taskRepository.findById(relatedTaskId)
					.orElseThrow(NotFoundTaskException::new);
			task.addRelatedTask(savedRelatedTask);
		}
	}
}
