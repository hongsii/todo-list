package com.hongsii.todolist.controller;

import com.hongsii.todolist.controller.response.ApiResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiCommonAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ApiResponse handleValidException(HttpMessageNotReadableException e) throws Exception {
		ApiResponse<String> exception = ApiResponse.badRequest("잘못된 요청입니다.");
		log.error("[{}] {}", exception.getStatusCode(), exception.getMessage());
		return exception;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse<String> handleValidException(MethodArgumentNotValidException e) {
		BindingResult result = e.getBindingResult();
		FieldError fieldError = result.getFieldError();
		String errorMessage = String.format("잘못된 요청입니다. [파라미터 : %s, 메세지 : %s]", fieldError.getField(), fieldError.getDefaultMessage());
		ApiResponse<String> exception = ApiResponse.badRequest(errorMessage);
		log.error("[{}] {}", exception.getStatusCode(), exception.getMessage());
		return exception;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ApiResponse<String> handleValidException(ConstraintViolationException e) {
		StringBuilder errorMessage = new StringBuilder("잘못된 요청입니다.");
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			errorMessage.append(
					String.format("- 파라미터 : %s, 메세지 : %s",
							violation.getPropertyPath().toString(), violation.getMessage()
					)
			);
		}
		ApiResponse<String> exception = ApiResponse.badRequest(errorMessage.toString());
		log.error("[{}] {}", exception.getStatusCode(), exception.getMessage());
		return exception;
	}
}
