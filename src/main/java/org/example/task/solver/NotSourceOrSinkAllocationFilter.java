package org.example.task.solver;

import org.example.projectjobscheduling.JobType;
import org.example.task.Plan;
import org.example.task.TaskSchedule;
import org.optaplanner.core.api.domain.entity.PinningFilter;

public class NotSourceOrSinkAllocationFilter implements PinningFilter<TaskSchedule, Plan> {

    @Override
    public boolean accept(TaskSchedule taskSchedule, Plan plan) {
        JobType jobType = plan.getTask().getJobType();
        return jobType == JobType.SOURCE || jobType == JobType.SINK;
    }

}
