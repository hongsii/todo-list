package com.hongsii.todolist.exception;

public class NotFoundTaskException extends RuntimeException {

	public NotFoundTaskException() {
		super("This task does not exist");
	}

	public NotFoundTaskException(String message) {
		super(message);
	}
}
