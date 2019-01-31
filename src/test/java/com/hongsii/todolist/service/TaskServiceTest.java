package com.hongsii.todolist.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.domain.TaskRelation;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import com.hongsii.todolist.exception.NotFoundTaskException;
import com.hongsii.todolist.repository.TaskRepository;
import com.hongsii.todolist.service.dto.TaskDto;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskRepository taskRepository;

	/* Default test target */
	private List<Task> savedTasks;
	private Task CHORE;
	private Task LAUNDRY;
	private Task CLEANING;
	private Task CLEANING_ROOM;

	@Before
	public void setUp() throws Exception {
		CHORE = taskRepository.save(Task.builder().content("집안일").build());
		TaskRelation ONE_RELATION = TaskRelation.builder().superTasks(asList(CHORE)).build();
		LAUNDRY = taskRepository.save(Task.builder().content("빨래").taskRelation(ONE_RELATION).build());
		CLEANING = taskRepository.save(Task.builder().content("청소").taskRelation(ONE_RELATION).build());
		TaskRelation TWO_RELATION = TaskRelation.builder().superTasks(asList(CHORE, CLEANING)).build();
		CLEANING_ROOM = taskRepository.save(Task.builder().content("방청소").taskRelation(TWO_RELATION).build());
		savedTasks = asList(CHORE, LAUNDRY, CLEANING, CLEANING_ROOM);
	}

	@After
	public void tearDown() throws Exception {
		taskRepository.deleteAll();
	}

	@Test
	public void create() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");

		TaskDto.Response created = taskService.create(request);

		assertThat(created.getId()).isNotNull();
		assertThat(taskRepository.count()).isEqualTo(savedTasks.size() + 1);
	}

	@Test
	public void createWithSuperTask() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");
		request.setSuperTaskIds(asList(CHORE.getId(), LAUNDRY.getId()));

		TaskDto.Response created = taskService.create(request);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getSuperTaskIds()).hasSize(2).isEqualTo(request.getSuperTaskIds());
		assertThat(taskRepository.count()).isEqualTo(savedTasks.size() + 1);
	}

	@Test(expected = NotFoundTaskException.class)
	public void createExceptionWhenSuperTaskNotExists() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");
		request.setSuperTaskIds(asList(Long.MAX_VALUE));

		taskService.create(request);
	}

	@Test
	public void findAllByPageable() {
		int halfOfSavedTasks = (int) Math.ceil(savedTasks.size() / 2);
		PageRequest pageRequest = PageRequest.of(halfOfSavedTasks - 1, halfOfSavedTasks);

		Page<TaskDto.Response> response = taskService.findAll(pageRequest);

		assertThat(response.getSize()).isEqualTo(halfOfSavedTasks);
	}

	@Test
	public void findById() {
		Long id = CHORE.getId();

		TaskDto.Response response = taskService.findById(id);

		assertThat(response.getId()).isEqualTo(id);
	}

	@Test(expected = NotFoundTaskException.class)
	public void findByIdExceptionWhenTaskNotExists() {
		taskService.findById(Long.MAX_VALUE);
	}

	@Test
	public void complete() {
		Long id = CLEANING_ROOM.getId();

		boolean isCompleted = taskService.complete(id);

		assertThat(isCompleted).isTrue();
	}

	@Test(expected = CannotCompleteTaskException.class)
	public void notCompleteWhenSubTaskIsNotCompleted() {
		Long id = CHORE.getId();

		taskService.complete(id);
	}

	@Test
	public void update() {
		TaskDto.Update update = new TaskDto.Update();
		update.setContent("방청소는 집에서");
		update.setCompleted(true);

		TaskDto.Response updated = taskService.update(CLEANING_ROOM.getId(), update);

		assertThat(updated.getContent()).isEqualTo(update.getContent());
		assertThat(updated.isCompleted()).isEqualTo(update.isCompleted());
	}
}