package com.hongsii.todolist.exception;

public class DuplicateRelatedTaskException extends RuntimeException {

	public DuplicateRelatedTaskException() {
		super("There is duplicated related task");
	}
}
