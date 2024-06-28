package org.example.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.projectjobscheduling.JobType;

import java.util.Collections;
import java.util.List;

/**
 * 工序节点
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Process {

    private String id;

    /**
     * 派工单
     */
    private WorkOrder workOrder;

    /**
     * 工序名称
     */
    private String processName;

    /**
     * 工序类型
     */
    private TaskType taskType;

    private JobType jobType;

    /**
     * 工序单件耗时
     */
    private Integer duration;

    /**
     * 后置工序
     */
    @EqualsAndHashCode.Exclude
    private List<Process> nextProcessList;

    /**
     * 前置工序
     */
    @EqualsAndHashCode.Exclude
    private List<Process> prevProcessList;

    public Process(String id, WorkOrder workOrder, String processName, TaskType taskType, JobType jobType,
                   Integer duration, List<Process> nextProcessList) {
        this.id = id;
        this.workOrder = workOrder;
        this.processName = processName;
        this.taskType = taskType;
        this.jobType = jobType;
        this.duration = duration;
        this.nextProcessList = nextProcessList;
        this.prevProcessList = Collections.emptyList();
    }
}