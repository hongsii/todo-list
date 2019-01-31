package com.hongsii.todolist.service;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.exception.NotFoundTaskException;
import com.hongsii.todolist.repository.TaskRepository;
import com.hongsii.todolist.service.dto.TaskDto;
import java.util.List;
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
		addSuperTasks(request.getSuperTaskIds(), created);
		return TaskDto.Response.of(created);
	}

	private void addSuperTasks(List<Long> superTaskIds, Task target) {
		for (Long superTaskId : superTaskIds) {
			Task superTask = taskRepository.findById(superTaskId)
					.orElseThrow(() -> new NotFoundTaskException(superTaskId));
			target.addSuperTask(superTask);
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

	@Transactional
	public boolean complete(Long id) {
		Task target = taskRepository.findById(id)
				.orElseThrow(NotFoundTaskException::new);
		return target.complete();
	}

	@Transactional
	public TaskDto.Response update(Long id, TaskDto.Update update) {
		Task updated = taskRepository.findById(id)
				.map(update::apply)
				.orElseThrow(NotFoundTaskException::new);
		return TaskDto.Response.of(updated);
	}
}
