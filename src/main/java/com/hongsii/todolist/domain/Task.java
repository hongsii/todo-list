package com.hongsii.todolist.domain;

import com.hongsii.todolist.exception.AlreadyCompletedTaskException;
import com.hongsii.todolist.exception.CannotCompleteTaskException;
import com.hongsii.todolist.exception.DuplicateRelatedTaskException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

	@ManyToMany
	@JoinTable(name = "task_relation",
			joinColumns = {
					@JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "related_task_id", referencedColumnName = "id", nullable = false)})
	private List<Task> relatedTasks;

	@ManyToMany(mappedBy = "relatedTasks")
	private List<Task> relatedByTasks;

	@Column
	private boolean isCompleted;

	@Builder
	public Task(Long id, String content, List<Task> relatedTasks, List<Task> relatedByTasks, boolean isCompleted) {
		this.id = id;
		this.content = content;
		this.relatedTasks = (relatedTasks != null) ? relatedTasks:new ArrayList<>();
		this.relatedByTasks = (relatedByTasks != null) ? relatedByTasks:new ArrayList<>();
		this.isCompleted = isCompleted;
	}

	public void addRelatedTask(Task relatedTask) {
		if (relatedTasks.contains(relatedTask)) {
			throw new DuplicateRelatedTaskException();
		}
		relatedTasks.add(relatedTask);
	}

	public boolean isAllCompletedRelatedTask() {
		long completedRelatedTaskCount = relatedByTasks.stream()
				.filter(Task::isCompleted)
				.count();
		return relatedByTasks.size() == completedRelatedTaskCount;
	}

	public boolean complete() {
		if (isCompleted) {
			throw new AlreadyCompletedTaskException("이미 완료된 작업입니다.");
		}
		if (!isAllCompletedRelatedTask()) {
			throw new CannotCompleteTaskException("참조 중인 작업이 완료되지 않았습니다.");
		}
		return isCompleted = true;
	}
}
