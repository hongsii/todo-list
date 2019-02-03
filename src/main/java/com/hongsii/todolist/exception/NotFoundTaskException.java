package com.hongsii.todolist.exception;

import java.util.List;
import java.util.stream.Collectors;

public class NotFoundTaskException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "등록되지 않은 작업입니다.";

	public NotFoundTaskException() {
		super(DEFAULT_MESSAGE);
	}

	public NotFoundTaskException(Long id) {
		super(DEFAULT_MESSAGE + "[id=" + id + "]");
	}

	public NotFoundTaskException(List<Long> ids) {
		super(DEFAULT_MESSAGE
				+ "[ids="
				+ ids.stream()
					.map(id -> id.toString())
					.collect(Collectors.joining(", "))
				+ "]");
	}
}
