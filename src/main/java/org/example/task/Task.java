package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.projectjobscheduling.JobType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @EqualsAndHashCode.Exclude
    private WorkOrder workOrder;

    private String taskName;
    private Integer duration;

    /**
     * 后续任务列表
     */
    @EqualsAndHashCode.Exclude
    private List<Task> nextTaskList;

    private TaskType taskType;

    private JobType jobType;

    /**
     * 可选的工作中心
     */
    private List<WorkCenter> workCenterList;


}


