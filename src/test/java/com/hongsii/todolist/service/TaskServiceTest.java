package com.hongsii.todolist.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.hongsii.todolist.domain.Task;
import com.hongsii.todolist.exception.NotFoundTaskException;
import com.hongsii.todolist.repository.TaskRepository;
import com.hongsii.todolist.service.dto.TaskDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskRepository taskRepository;


	/**
	 *
	 * | 할일 | 작성일시 | 최종수정일시 | 완료처리 |
	 * |----|-------------|---------------------|----------|---------------------|
	 * | 1 | 집안일 | 2018-04-01 10:00:00 | 2018-04-01 13:00:00 |  |
	 * | 2 | 빨래 @1 | 2018-04-01 11:00:00 | 2018-04-01 11:00:00 |  |
	 * | 3 | 청소 @1 | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |  |
	 * | 4 | 방청소 @1 @3 | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |  |
	 */

	private static final int DEFAULT_ENTITY_COUNT = 4;

	@Before
	public void setUp() throws Exception {
		Task chore = taskRepository.save(Task.builder().content("집안일").build());
		Task laundry = taskRepository.save(Task.builder().content("빨래").relatedTasks(asList(chore)).build());
		Task cleaning = taskRepository.save(Task.builder().content("청소").relatedTasks(asList(chore)).build());
		Task cleaningRoom = taskRepository.save(Task.builder().content("방청소").relatedTasks(asList(chore, cleaning)).build());
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
		request.setRelatedTaskIds(asList(1L));

		TaskDto.Response created = taskService.create(request);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getRelatedTaskIds()).hasSize(1).containsExactly(1L);
		assertThat(taskRepository.count()).isEqualTo(DEFAULT_ENTITY_COUNT + 1);
	}

	@Test(expected = NotFoundTaskException.class)
	public void createButNotExistsRelatedTask() {
		TaskDto.Create request = new TaskDto.Create();
		request.setContent("설거지");
		request.setRelatedTaskIds(asList(9999L));

		taskService.create(request);
	}
}