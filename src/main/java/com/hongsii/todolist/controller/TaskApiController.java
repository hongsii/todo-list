package com.hongsii.todolist.controller;

import com.hongsii.todolist.controller.response.ApiResponse;
import com.hongsii.todolist.service.TaskService;
import com.hongsii.todolist.service.dto.TaskDto;
import com.hongsii.todolist.service.dto.TaskDto.Response;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskApiController {

	private final TaskService taskService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Response> create(@RequestBody @Valid TaskDto.Create request) {
		return ApiResponse.created(taskService.create(request));
	}
}
