package com.hongsii.todolist.exception;

public class NotFoundRelatedTaskException extends RuntimeException {

	public NotFoundRelatedTaskException() {
		super("This related task not exists");
	}

	public NotFoundRelatedTaskException(String message) {
		super(message);
	}
}
