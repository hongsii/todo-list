package com.hongsii.todolist.controller;

import com.hongsii.todolist.controller.response.ApiResponse;
import com.hongsii.todolist.exception.AlreadyCompletedTaskException;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import com.hongsii.todolist.exception.DuplicateRelatedTaskException;
import com.hongsii.todolist.exception.NotFoundTaskException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = TaskApiController.class)
public class TaskApiControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({
			NotFoundTaskException.class, DuplicateRelatedTaskException.class,
			AlreadyCompletedTaskException.class, CannotCompleteTaskException.class
	})
	public ApiResponse handleBadRequest(Exception e) {
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ApiResponse exceptionResponse = ApiResponse.of(HttpStatus.BAD_REQUEST, e);
		log.error("[{}] {}", badRequest.getReasonPhrase(), exceptionResponse);
		return exceptionResponse;
	}
}
