package com.hongsii.todolist.domain;

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
import lombok.Singular;
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
	@JoinTable(name = "related_task",
			joinColumns = {
			@JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)},
			inverseJoinColumns = {
			@JoinColumn(name = "related_task_id", referencedColumnName = "id", nullable = false)})
	@Singular
	private List<Task> relatedTasks;

	@Builder
	public Task(Long id, String content, List<Task> relatedTasks) {
		this.id = id;
		this.content = content;
		this.relatedTasks = (relatedTasks != null) ? relatedTasks:new ArrayList<>();
	}

	public void addRelatedTask(Task relatedTask) {
		if (relatedTasks.contains(relatedTask)) {
			throw new DuplicateRelatedTaskException();
		}
		relatedTasks.add(relatedTask);
	}
}
