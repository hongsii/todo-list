package com.hongsii.todolist.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongsii.todolist.service.TaskService;
import com.hongsii.todolist.service.dto.TaskDto;
import com.hongsii.todolist.service.dto.TaskDto.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskApiController.class)
public class TaskApiControllerTest {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TaskService taskService;

	@Test
	public void create() throws Exception {
		TaskDto.Response response = Response.builder()
				.id(4L)
				.content("방청소")
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.superTaskIds(asList(1L, 3L))
				.build();
		given(taskService.create(any(TaskDto.Create.class))).willReturn(response);

		TaskDto.Create request = new TaskDto.Create();
		request.setContent("방청소");
		request.setSuperTaskIds(asList(1L, 3L));

		ResultActions result = mockMvc.perform(post("/api/tasks")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
		);

		result.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.task.id").value(4))
				.andDo(print());
	}

	@Test
	public void findAll() throws Exception {
		Page<TaskDto.Response> response = new PageImpl(
				asList(
						Response.builder()
								.id(3L)
								.content("청소")
								.createdDate(parseDate("2018-04-01 12:00:00"))
								.modifiedDate(parseDate("2018-04-01 13:00:00"))
								.superTaskIds(asList(1L))
								.build(),
						Response.builder()
								.id(4L)
								.content("방청소")
								.createdDate(parseDate("2018-04-01 12:00:00"))
								.modifiedDate(parseDate("2018-04-01 13:00:00"))
								.superTaskIds(asList(1L, 3L))
								.build()
				)
		);
		given(taskService.findAll(any(Pageable.class))).willReturn(response);

		ResultActions result = mockMvc.perform(get("/api/tasks")
				.param("page", "2")
				.param("size", "2")
		);

		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.tasks.content.length()").value(2))
				.andDo(print());
	}

	private LocalDateTime parseDate(String rawDate) {
		return LocalDateTime.parse(rawDate, DATE_FORMATTER);
	}

	@Test
	public void findById() throws Exception {
		Response response = Response.builder()
				.id(3L)
				.content("청소")
				.createdDate(parseDate("2018-04-01 12:00:00"))
				.modifiedDate(parseDate("2018-04-01 13:00:00"))
				.superTaskIds(asList(1L))
				.build();
		given(taskService.findById(any(Long.class))).willReturn(response);

		ResultActions result = mockMvc.perform(get("/api/tasks/3"));

		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.task.id").value(3))
				.andDo(print());
	}

	@Test
	public void update() throws Exception {
		Response response = Response.builder()
				.id(3L)
				.content("청소는 집에서")
				.createdDate(parseDate("2018-04-01 12:00:00"))
				.modifiedDate(parseDate("2018-04-01 13:00:00"))
				.superTaskIds(asList(1L))
				.isCompleted(false)
				.build();
		given(taskService.update(any(Long.class), any(TaskDto.Update.class)))
				.willReturn(response);

		TaskDto.Update update = new TaskDto.Update();
		update.setContent("청소는 집에서");

		ResultActions result = mockMvc.perform(put("/api/tasks/3")
				.content(objectMapper.writeValueAsString(update))
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
		);

		result.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	public void complete() throws Exception {
		given(taskService.complete(any(Long.class))).willReturn(true);

		ResultActions result = mockMvc.perform(patch("/api/tasks/3/complete"));

		result.andExpect(status().isOk())
				.andDo(print());
	}
}