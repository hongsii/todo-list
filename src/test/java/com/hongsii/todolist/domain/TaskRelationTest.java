package com.hongsii.todolist.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.exception.DuplicatedTaskException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class TaskRelationTest {

	public static final TaskRelation ALL_COMPLETED_SUBTASK = TaskRelation.builder()
			.subTasks(new ArrayList<>(
					asList(
							Task.builder().id(2L).content("빨래").isCompleted(true).build(),
							Task.builder().id(3L).content("청소").isCompleted(true).build()
					)
			))
			.superTasks(new ArrayList<>(
					asList(
							Task.builder().id(4L).content("방청소").isCompleted(true).build()
					)
			))
			.build();
	public static final TaskRelation NOT_COMPLETED_SUBTASK = TaskRelation.builder()
			.subTasks(new ArrayList<>(
					asList(
							Task.builder().id(2L).content("빨래").isCompleted(true).build(),
							Task.builder().id(3L).content("청소").isCompleted(false).build()
					)
			))
			.build();

	private TaskRelation taskRelation;

	@Before
	public void setUp() throws Exception {
		taskRelation = ALL_COMPLETED_SUBTASK;
	}

	@Test(expected = DuplicatedTaskException.class)
	public void addSubTaskExceptionWhenTaskWasAlreadyAdded() {
		taskRelation.addSubTask(Task.builder().id(2L).build());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addSubTaskExceptionWhenTaskIsNotSaved() {
		taskRelation.addSubTask(Task.builder().build());
	}

	@Test(expected = DuplicatedTaskException.class)
	public void addSuperTaskExceptionWhenTaskWasAlreadyAdded() {
		taskRelation.addSuperTask(Task.builder().id(4L).build());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addSuperTaskExceptionWhenTaskIsNotSaved() {
		taskRelation.addSuperTask(Task.builder().build());
	}

	@Test
	public void isAllCompletedSubTasks() {
		boolean isAllCompleted = taskRelation.isAllCompletedSubTasks();

		assertThat(isAllCompleted).isTrue();
	}

	@Test
	public void isNotCompletedSubTasks() {
		taskRelation = NOT_COMPLETED_SUBTASK;

		boolean isAllCompleted = taskRelation.isAllCompletedSubTasks();

		assertThat(isAllCompleted).isFalse();
	}
}