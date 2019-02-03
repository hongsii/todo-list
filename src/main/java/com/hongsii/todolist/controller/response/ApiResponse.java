package com.hongsii.todolist.controller.response;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ApiResponse<T> {

	public static final ApiResponse<String> DEFAULT_OK = new ApiResponse<>(HttpStatus.OK);

	private int statusCode;
	private String message;
	private T data;

	private ApiResponse(HttpStatus httpStatus) {
		setStatusCode(httpStatus);
	}

	private ApiResponse(HttpStatus httpStatus, T data) {
		setStatusCode(httpStatus);
		this.data = data;
	}

	private ApiResponse(HttpStatus httpStatus, Exception exception, T data) {
		setStatusCode(httpStatus);
		this.message = exception.getMessage();
		this.data = data;
	}

	private ApiResponse(HttpStatus httpStatus, String message, T data) {
		setStatusCode(httpStatus);
		this.message = message;
		this.data = data;
	}

	private void setStatusCode(HttpStatus httpStatus) {
		statusCode = httpStatus.value();
		message = httpStatus.getReasonPhrase();
	}

	public static <T> ApiResponse created(T data) {
		return ApiResponse.of(HttpStatus.CREATED, data);
	}

	public static <T> ApiResponse ok(T data) {
		return ApiResponse.of(HttpStatus.OK, data);
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
		return new ApiResponse<>(httpStatus, data);
	}

	public static ApiResponse<String> of(HttpStatus httpStatus, Exception exception) {
		return new ApiResponse<>(httpStatus, exception, "");
	}

	public static ApiResponse<String> badRequest(String message) {
		return new ApiResponse<>(HttpStatus.BAD_REQUEST, message, "");
	}
}
