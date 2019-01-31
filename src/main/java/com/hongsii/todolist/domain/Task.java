package com.hongsii.todolist.domain;

import com.hongsii.todolist.exception.AlreadyCompletedTaskException;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Task extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	@Size(max = 30)
	private String content;

	@Embedded
	private TaskRelation taskRelation;

	@Column
	private boolean isCompleted;

	@Builder
	public Task(Long id, String content, TaskRelation taskRelation, boolean isCompleted) {
		this.id = id;
		this.content = content;
		this.taskRelation = taskRelation != null ? taskRelation:TaskRelation.builder().build();
		this.isCompleted = isCompleted;
	}

	public boolean complete() {
		if (isCompleted) {
			throw new AlreadyCompletedTaskException("이미 완료된 작업입니다.");
		}
		if (!taskRelation.isAllCompletedSubTasks()) {
			throw new CannotCompleteTaskException("하위 작업이 모두 완료되지 않았습니다.");
		}
		return isCompleted = true;
	}

	public Task update(String content, boolean isCompleted) {
		this.content = content;
		if (isCompleted) {
			complete();
		}
		return this;
	}

	public void addSuperTask(Task task) {
		taskRelation.addSuperTask(task);
	}
}
