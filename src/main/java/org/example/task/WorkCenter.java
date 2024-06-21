package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PlanningEntity
public class WorkCenter {
    /**
     * 工作中心名称
     */
    private String workCenterName;

    /**
     * 该工作中心可以干的工序类型列表
     */
    private List<TaskType> taskTypes;

    /**
     * 优先级 1-10，越小越靠前
     */
    private Integer priority;

    @PlanningListVariable
    private List<Task> taskList;
}