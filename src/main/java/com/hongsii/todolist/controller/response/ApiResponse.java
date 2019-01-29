package com.hongsii.todolist.controller.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

	private int statusCode;
	private T data;

	private ApiResponse(int statusCode, T data) {
		this.statusCode = statusCode;
		this.data = data;
	}

	public static <T> ApiResponse created(T data) {
		return ApiResponse.of(HttpStatus.CREATED, data);
	}

	public static <T> ApiResponse ok(T data) {
		return ApiResponse.of(HttpStatus.OK, data);
	}

	public static <T> ApiResponse of(HttpStatus httpStatus, T data) {
		return new ApiResponse(httpStatus.value(), data);
	}
}
