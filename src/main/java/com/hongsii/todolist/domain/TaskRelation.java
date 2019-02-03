package com.hongsii.todolist.domain;

import com.hongsii.todolist.exception.DuplicatedTaskException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class TaskRelation {

	@ManyToMany
	@JoinTable(
			name = "task_relation",
			joinColumns = {
					@JoinColumn(name = "sub_task_id", referencedColumnName = "id", nullable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "super_task_id", referencedColumnName = "id", nullable = false)})
	private List<Task> superTasks;

	@ManyToMany(mappedBy = "taskRelation.superTasks")
	private List<Task> subTasks;

	@Builder
	public TaskRelation(List<Task> subTasks, List<Task> superTasks) {
		this.subTasks = subTasks != null ? subTasks:new ArrayList<>();
		this.superTasks = superTasks != null ? superTasks:new ArrayList<>();
	}

	public void addSubTask(Task task) {
		validate(task);
		if (subTasks.contains(task)) {
			throw new DuplicatedTaskException("이미 등록된 하위 작업입니다.");
		}
		subTasks.add(task);
	}

	public void addSuperTask(Task task) {
		validate(task);
		if (superTasks.contains(task)) {
			throw new DuplicatedTaskException("이미 등록된 상위 작업입니다.");
		}
		superTasks.add(task);
	}

	private void validate(Task task) {
		if (task.getId() == null) {
			throw new IllegalArgumentException("등록되지 않은 작업입니다.");
		}
	}

	public boolean isAllCompletedSubTasks() {
		return subTasks.stream()
				.allMatch(Task::isCompleted);
	}

	public List<Long> getSuperTaskIds() {
		return superTasks.stream()
				.map(Task::getId)
				.collect(Collectors.toList());
	}

	public void updateSuperTasks(List<Task> update) {
		for (Task task : update) {
			if (!superTasks.contains(task)) {
				superTasks.add(task);
			}
		}
	}

	public void removeSuperTaskIfNotContainsIn(List<Task> update) {
		superTasks.removeIf(task -> !update.contains(task));
	}
}
