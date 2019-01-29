package com.hongsii.todolist.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hongsii.todolist.domain.Task;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDto {

	@Getter
	@Setter
	public static class Create {

		@NotEmpty
		private String content;
		private List<Long> relatedTaskIds;

		public Task toEntity() {
			return Task.builder()
					.content(content)
					.build();
		}

		public List<Long> getRelatedTaskIds() {
			if (relatedTaskIds == null) {
				return Collections.emptyList();
			}
			return relatedTaskIds;
		}
	}

	@Getter
	@Setter
	public static class Response {

		private Long id;
		private String content;
		@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createdDate;
		@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
		private LocalDateTime modifiedDate;
		private List<Long> relatedTaskIds;

		@Builder
		public Response(Long id, String content, LocalDateTime createdDate,
				LocalDateTime modifiedDate, List<Long> relatedTaskIds) {
			this.id = id;
			this.content = content;
			this.createdDate = createdDate;
			this.modifiedDate = modifiedDate;
			this.relatedTaskIds = relatedTaskIds;
		}

		public static Response of(Task task) {
			return Response.builder()
					.id(task.getId())
					.content(task.getContent())
					.createdDate(task.getCreatedDate())
					.modifiedDate(task.getModifiedDate())
					.relatedTaskIds(task.getRelatedTasks().stream()
							.map(Task::getId)
							.collect(Collectors.toList())
					)
					.build();
		}
	}

	@Getter
	public static class ResponseOne {

		private Response task;

		public ResponseOne(Response task) {
			this.task = task;
		}
	}

	@Getter
	public static class ResponsePage {

		private Page<Response> tasks;

		public ResponsePage(Page<Response> tasks) {
			this.tasks = tasks;
		}
	}
}
