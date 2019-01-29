package com.hongsii.todolist.exception;

public class CannotCompleteTaskException extends RuntimeException {

	public CannotCompleteTaskException() {
		super("This task can't be completed");
	}

	public CannotCompleteTaskException(String message) {
		super(message);
	}
}
