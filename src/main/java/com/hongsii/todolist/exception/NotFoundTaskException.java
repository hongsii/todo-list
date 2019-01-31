package com.hongsii.todolist.exception;

public class NotFoundTaskException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "This task does not exist";

	public NotFoundTaskException() {
		super(DEFAULT_MESSAGE);
	}

	public NotFoundTaskException(Long id) {
		super(DEFAULT_MESSAGE + "[id=" + id + "]");
	}
}
