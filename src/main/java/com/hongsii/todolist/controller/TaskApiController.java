package com.hongsii.todolist.controller;

import com.hongsii.todolist.controller.response.ApiResponse;
import com.hongsii.todolist.service.TaskService;
import com.hongsii.todolist.service.dto.TaskDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskApiController {

	private final TaskService taskService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<TaskDto.ResponseOne> create(@RequestBody @Valid TaskDto.Create request) {
		return ApiResponse.created(new TaskDto.ResponseOne(taskService.create(request)));
	}

	@GetMapping
	public ApiResponse<Page<TaskDto.Response>> findAll(@PageableDefault Pageable pageable) {
		return ApiResponse.ok(new TaskDto.ResponsePage(taskService.findAll(pageable)));
	}

	@GetMapping("{id}")
	public ApiResponse<TaskDto.ResponseOne> findById(@PathVariable Long id) {
		return ApiResponse.ok(new TaskDto.ResponseOne(taskService.findById(id)));
	}

	@PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponse<TaskDto.Response> update(@PathVariable Long id, @RequestBody @Valid TaskDto.Update update) {
		return ApiResponse.ok(new TaskDto.ResponseOne(taskService.update(id, update)));
	}

	@PatchMapping(value = "{id}/complete")
	public ApiResponse complete(@PathVariable Long id) {
		taskService.complete(id);
		return ApiResponse.DEFAULT_OK;
	}
}
