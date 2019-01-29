package com.hongsii.todolist.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.exception.NotFoundRelatedTaskException;
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

	private int DEFAULT_ENTITY_COUNT = 4;
	private List<Task> savedTasks;

	@Before
	public void setUp() throws Exception {
		Task chore = taskRepository.save(Task.builder().content("집안일").build());
		Task laundry = taskRepository.save(Task.builder().content("빨래").relatedTasks(asList(chore)).build());
		Task cleaning = taskRepository.save(Task.builder().content("청소").relatedTasks(asList(chore)).build());
		Task cleaningRoom = taskRepository.save(Task.builder().content("방청소").relatedTasks(asList(chore, cleaning)).build());
		savedTasks = taskRepository.findAll();
		DEFAULT_ENTITY_COUNT = savedTasks.size();
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
		assertThat(taskRepository.count()).isEqualTo(DEFAULT_ENTITY_COUNT + 1);
	}

	@Test
	public void createWithRelatedTask() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");
		Long relatedId1 = savedTasks.get(0).getId(), relatedId2 = savedTasks.get(1).getId();
		request.setRelatedTaskIds(asList(relatedId1, relatedId2));

		TaskDto.Response created = taskService.create(request);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getRelatedTaskIds()).hasSize(2).isEqualTo(request.getRelatedTaskIds());
		assertThat(taskRepository.count()).isEqualTo(DEFAULT_ENTITY_COUNT + 1);
	}

	@Test(expected = NotFoundRelatedTaskException.class)
	public void createExceptionWhenRelatedTaskNotExists() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");
		request.setRelatedTaskIds(asList(Long.MAX_VALUE));

		taskService.create(request);
	}

	@Test
	public void findAllByPageable() {
		int lastPage = 2;
		Page<TaskDto.Response> savedTasks = taskService.findAll(PageRequest.of(lastPage - 1, 2));

		assertThat(savedTasks.getSize()).isEqualTo(2);
	}

	@Test
	public void findById() {
		Long id = savedTasks.get(0).getId();

		TaskDto.Response response = taskService.findById(id);

		assertThat(response.getId()).isEqualTo(id);
	}

	@Test(expected = NotFoundTaskException.class)
	public void findByIdExceptionWhenTaskNotExists() {
		taskService.findById(Long.MAX_VALUE);
	}
}