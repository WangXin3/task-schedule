package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String taskName;
    private Duration duration;

    private List<Task> prevTaskList;

    private TaskType type;

    /**
     * 任务在工序路线图中的层级，为了排序
     */
    private Integer deep;

    /**
     * 交期
     */
    private Integer deadLine;
}


