package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectjobscheduling.JobType;
import org.example.task.solver.DelayStrengthComparator;
import org.example.task.solver.PredecessorsDoneDateUpdatingVariableListener;
import org.example.task.solver.WorkCenterStrengthWeightFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

import java.util.List;

/**
 * @author wangxin
 * @since 2024/6/28 10:09
 */

//@PlanningEntity(pinningFilter = NotSourceOrSinkAllocationFilter.class)
@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private Task task;

    /**
     * 源计划
     */
    private Plan sourcePlan;

    /**
     * 尾计划
     */
    private Plan sinkPlan;


    /**
     * 前置计划
     */
    private List<Plan> predecessorPlanList;

    /**
     * 后置计划
     */
    private List<Plan> successorPlanList;


    /**
     * 执行模式
     */
    // Planning variables: changes during planning, between score calculations.
    @PlanningVariable(strengthComparatorClass = WorkCenterStrengthWeightFactory.class,
            valueRangeProviderRefs = "workCenterRange")
    private WorkCenter workCenter;
    /**
     * 延迟
     */
    @PlanningVariable(strengthComparatorClass = DelayStrengthComparator.class,
            valueRangeProviderRefs = "delayRange")
    private Integer delay; // In hours

    /**
     * 前置分配结束时间
     */
    // Shadow variables
    @ShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
            sourceVariableName = "delay")
    private Integer predecessorsDoneDate;

    /**
     * 执行模式的取值区间
     *
     * @return /
     */
    @ValueRangeProvider(id = "workCenterRange")
    public List<WorkCenter> getWorkCenterRange() {
        return task.getWorkCenterList();
    }

    /**
     * 延期的取值区间
     *
     * @return /
     */
    @ValueRangeProvider(id = "delayRange")
    public CountableValueRange<Integer> getDelayRange() {
        return ValueRangeFactory.createIntValueRange(0, 500);
    }

    /**
     * 获取该分配的开始时间
     *
     * @return /
     */
    public Integer getStartDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay);
    }

    /**
     * 获取该分配的结束时间
     *
     * @return /
     */
    public Integer getEndDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay) + task.getDuration();
    }

    /**
     * 获取该任务所在的项目
     *
     * @return /
     */
    public WorkOrder getWorkOrder() {
        return task.getWorkOrder();
    }

    /**
     * 获取该派工单的结束时间
     *
     * @return /
     */
    public int getProjectCriticalPathEndDate() {
        return task.getWorkOrder().getCriticalPathEndDate();
    }

    /**
     * 获取该任务的类型
     *
     * @return /
     */
    public JobType getJobType() {
        return task.getJobType();
    }
}
