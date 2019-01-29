package com.hongsii.todolist.exception;

public class AlreadyCompletedTaskException extends RuntimeException {

	public AlreadyCompletedTaskException() {
		super("This task is already completed");
	}

	public AlreadyCompletedTaskException(String message) {
		super(message);
	}
}
