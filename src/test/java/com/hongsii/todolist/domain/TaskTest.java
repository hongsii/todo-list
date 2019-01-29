package com.hongsii.todolist.domain;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.exception.AlreadyCompletedTaskException;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import com.hongsii.todolist.exception.DuplicateRelatedTaskException;
import java.util.List;
import org.junit.Test;

public class TaskTest {

	public static Task CHORES = Task.builder()
			.id(1L)
			.content("집안일")
			.build();

	@Test(expected = DuplicateRelatedTaskException.class)
	public void addRelatedTask() {
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedTasks(asList(CHORES))
				.build();
		task.addRelatedTask(CHORES);
	}

	@Test
	public void isRelatedByTask() {
		List<Task> allCompletedTasks = createRelatedTask(true);
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedByTasks(allCompletedTasks)
				.build();

		assertThat(task.isAllCompletedRelatedTask()).isTrue();
	}

	@Test
	public void notRelatedByTask() {
		List<Task> relatedByTasksIncludeNotCompleted = asList(
				Task.builder().id(3L).content("빗질").isCompleted(true).build(),
				Task.builder().id(4L).content("걸레질").isCompleted(false).build()
		);
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedByTasks(relatedByTasksIncludeNotCompleted)
				.build();

		assertThat(task.isAllCompletedRelatedTask()).isFalse();
	}

	@Test
	public void complete() {
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.isCompleted(false)
				.build();

		boolean isCompleted = task.complete();

		assertThat(isCompleted).isTrue();
	}

	@Test(expected = AlreadyCompletedTaskException.class)
	public void completedAlready() {
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.isCompleted(true)
				.build();

		task.complete();
	}

	@Test
	public void completeWhenRelatedByTaskIsAllCompleted() {
		List<Task> completedTask = createRelatedTask(true);
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedByTasks(completedTask)
				.build();

		boolean isCompleted = task.complete();

		assertThat(isCompleted).isTrue();
	}

	@Test(expected = CannotCompleteTaskException.class)
	public void notCompleteWhenRelatedByTaskIsNotCompleted() {
		List<Task> notCompletedTask = createRelatedTask(false);
		Task task = Task.builder()
				.id(2L)
				.content("청소")
				.relatedByTasks(notCompletedTask)
				.build();

		task.complete();
	}

	private List<Task> createRelatedTask(boolean isAllCompleted) {
		return asList(
				Task.builder().id(3L).content("빗질").isCompleted(isAllCompleted).build(),
				Task.builder().id(4L).content("걸레질").isCompleted(isAllCompleted).build()
		);
	}
}