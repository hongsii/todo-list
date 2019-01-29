package com.hongsii.todolist.domain;

import static java.util.Arrays.asList;

import com.hongsii.todolist.exception.DuplicateRelatedTaskException;
import org.junit.Test;

public class TaskTest {

	public static Task chores = Task.builder()
			.id(1L)
			.content("집안일")
			.build();

	@Test(expected = DuplicateRelatedTaskException.class)
	public void addRelatedTask() {
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedTasks(asList(chores))
				.build();
		task.addRelatedTask(chores);
	}
}