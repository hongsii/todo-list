package com.hongsii.todolist.exception;

public class DuplicatedTaskException extends RuntimeException {

	public DuplicatedTaskException() {
		super("There is duplicated task");
	}

	public DuplicatedTaskException(String message) {
		super(message);
	}
}
