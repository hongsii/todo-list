package com.hongsii.todolist.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hongsii.todolist.domain.Task;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.Size;
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

		@Size(min = 1, max = 30)
		private String content;
		private List<Long> superTaskIds;

		public Task toEntity() {
			return Task.builder()
					.content(content)
					.build();
		}

		public List<Long> getSuperTaskIds() {
			return superTaskIds != null ? superTaskIds : Collections.emptyList();
		}
	}

	@Getter
	@Setter
	public static class Update {

		@Size(min = 1, max = 30)
		private String content;
		private List<Long> superTaskIds;
		private boolean isCompleted;

		public Task apply(Task task) {
			return task.update(content, isCompleted);
		}

		public List<Long> getSuperTaskIds() {
			return superTaskIds != null ? superTaskIds : Collections.emptyList();
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
		private List<Long> superTaskIds;
		private boolean isCompleted;

		@Builder
		public Response(Long id, String content, LocalDateTime createdDate,
				LocalDateTime modifiedDate, List<Long> superTaskIds, boolean isCompleted) {
			this.id = id;
			this.content = content;
			this.createdDate = createdDate;
			this.modifiedDate = modifiedDate;
			this.superTaskIds = superTaskIds;
			this.isCompleted = isCompleted;
		}

		public static Response of(Task task) {
			return Response.builder()
					.id(task.getId())
					.content(task.getContent())
					.createdDate(task.getCreatedDate())
					.modifiedDate(task.getModifiedDate())
					.superTaskIds(task.getTaskRelation().getSuperTaskIds())
					.isCompleted(task.isCompleted())
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

		private List<Response> tasks;
		private PageData pageData;

		public ResponsePage(Page<Response> tasks) {
			this.tasks = tasks.getContent();
			this.pageData = new PageData(tasks);
		}
	}

	@Getter
	public static class PageData {

		private int number;
		private int currentPage;
		private int totalPages;

		public PageData(Page<Response> page) {
			this.number = page.getNumber();
			this.currentPage = page.getPageable().getPageNumber();
			this.totalPages = page.getTotalPages();
		}
	}
}
