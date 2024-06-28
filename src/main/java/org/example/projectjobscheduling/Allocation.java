package org.example.projectjobscheduling;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.*;
import org.example.projectjobscheduling.solver.DelayStrengthComparator;
import org.example.projectjobscheduling.solver.ExecutionModeStrengthWeightFactory;
import org.example.projectjobscheduling.solver.NotSourceOrSinkAllocationFilter;
import org.example.projectjobscheduling.solver.PredecessorsDoneDateUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

import java.util.List;

@PlanningEntity(pinningFilter = NotSourceOrSinkAllocationFilter.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Allocation extends AbstractPersistable {

    /**
     * 任务
     */
    private Job job;

    /**
     * 源分配
     */
    private Allocation sourceAllocation;

    /**
     * 尾分配
     */
    private Allocation sinkAllocation;

    /**
     * 前置分配
     */
    private List<Allocation> predecessorAllocationList;

    /**
     * 后置分配
     */
    private List<Allocation> successorAllocationList;

    /**
     * 执行模式
     */
    // Planning variables: changes during planning, between score calculations.
    @PlanningVariable(strengthWeightFactoryClass = ExecutionModeStrengthWeightFactory.class)
    private ExecutionMode executionMode;

    /**
     * 延迟
     */
    @PlanningVariable(strengthComparatorClass = DelayStrengthComparator.class)
    private Integer delay; // In days

    /**
     * 前置分配结束时间
     */
    // Shadow variables
    @ShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
            sourceVariableName = "executionMode")
    @ShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class,
            sourceVariableName = "delay")
    private Integer predecessorsDoneDate;


    public Allocation(long id, Job job) {
        super(id);
        this.job = job;
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    /**
     * 执行模式的取值区间
     *
     * @return /
     */
    @ValueRangeProvider
    public List<ExecutionMode> getExecutionModeRange() {
        return job.getExecutionModeList();
    }

    /**
     * 延期的取值区间
     *
     * @return /
     */
    @ValueRangeProvider
    public CountableValueRange<Integer> getDelayRange() {
        return ValueRangeFactory.createIntValueRange(0, 500);
    }



    // ************************************************************************
    // Complex methods
    // ************************************************************************

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
        return predecessorsDoneDate + (delay == null ? 0 : delay)
                + (executionMode == null ? 0 : executionMode.getDuration());
    }

    /**
     * 获取该任务所在的项目
     *
     * @return /
     */
    public Project getProject() {
        return job.getProject();
    }

    /**
     * 获取该项目的结束时间
     *
     * @return /
     */
    public int getProjectCriticalPathEndDate() {
        return job.getProject().getCriticalPathEndDate();
    }

    /**
     * 获取该任务的类型
     *
     * @return /
     */
    public JobType getJobType() {
        return job.getJobType();
    }

}
