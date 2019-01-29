package com.hongsii.todolist.service;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.exception.NotFoundRelatedTaskException;
import com.hongsii.todolist.exception.NotFoundTaskException;
import com.hongsii.todolist.repository.TaskRepository;
import com.hongsii.todolist.service.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
					.orElseThrow(NotFoundRelatedTaskException::new);

			task.addRelatedTask(savedRelatedTask);
		}
	}

	@Transactional(readOnly = true)
	public Page<TaskDto.Response> findAll(Pageable pageable) {
		return taskRepository.findAll(pageable)
				.map(TaskDto.Response::of);
	}

	@Transactional(readOnly = true)
	public TaskDto.Response findById(Long id) {
		return taskRepository.findById(id)
				.map(TaskDto.Response::of)
				.orElseThrow(NotFoundTaskException::new);
	}
}
