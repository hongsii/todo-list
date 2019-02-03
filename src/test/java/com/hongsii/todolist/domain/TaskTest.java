package com.hongsii.todolist.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.domain.Task.TaskBuilder;
import com.hongsii.todolist.exception.AlreadyCompletedTaskException;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TaskTest {

	private TaskBuilder DEFAULT_TASK_BUILDER;

	@Before
	public void setUp() throws Exception {
		DEFAULT_TASK_BUILDER = Task.builder()
				.id(1L)
				.content("청소");
	}

	@Test
	public void complete() {
		Task task = DEFAULT_TASK_BUILDER
				.isCompleted(false)
				.build();

		boolean isCompleted = task.complete();

		assertThat(isCompleted).isTrue();
	}

	@Test(expected = AlreadyCompletedTaskException.class)
	public void completedAlready() {
		Task task = DEFAULT_TASK_BUILDER
				.isCompleted(true)
				.build();

		task.complete();
	}

	@Test
	public void completeWhenAllOfSubTasksAreCompleted() {
		Task task = DEFAULT_TASK_BUILDER
				.taskRelation(TaskRelationTest.ALL_COMPLETED_SUBTASK)
				.build();

		boolean isCompleted = task.complete();

		assertThat(isCompleted).isTrue();
	}

	@Test(expected = CannotCompleteTaskException.class)
	public void notCompleteWhenSubTaskIsNotCompleted() {
		Task task = DEFAULT_TASK_BUILDER
				.taskRelation(TaskRelationTest.NOT_COMPLETED_SUBTASK)
				.build();
		task.complete();
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotAddSuperTaskItself() {
		Task task = DEFAULT_TASK_BUILDER.build();

		task.addSuperTask(task);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotAddAlreadyCompletedSuperTask() {
		Task task = DEFAULT_TASK_BUILDER.build();

		task.addSuperTask(Task.builder().id(2L).isCompleted(true).build());
	}

	@Test
	public void updateSuperTasks() {
		Task task = Task.builder()
				.id(1L)
				.taskRelation(TaskRelation.builder()
						.superTasks(new ArrayList<>(
								asList(
										Task.builder().id(2L).build(),
										Task.builder().id(3L).build()
								)
						))
						.build()
				)
				.build();

		List<Task> update = asList(
				Task.builder().id(2L).build(),
				Task.builder().id(4L).build()
		);
		task.updateSuperTasks(update);

		assertThat(task.getTaskRelation().getSuperTaskIds())
				.hasSize(2).containsExactly(2L, 4L);
	}
}