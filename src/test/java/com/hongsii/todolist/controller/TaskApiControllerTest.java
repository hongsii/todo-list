package com.hongsii.todolist.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
				.relatedTaskIds(asList(1L, 3L))
				.build();
		given(taskService.create(any(TaskDto.Create.class))).willReturn(response);

		TaskDto.Create request = new TaskDto.Create();
		request.setContent("집안일");
		request.setRelatedTaskIds(asList(1L, 3L));

		ResultActions result = mockMvc.perform(post("/api/tasks")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
		);

		result.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.id").value(4))
				.andDo(print());
	}
}