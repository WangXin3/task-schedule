package org.example.projectjobscheduling.solver;

import org.example.projectjobscheduling.Allocation;
import org.example.projectjobscheduling.JobType;
import org.example.projectjobscheduling.Schedule;
import org.optaplanner.core.api.domain.entity.PinningFilter;

public class NotSourceOrSinkAllocationFilter implements PinningFilter<Schedule, Allocation> {

    @Override
    public boolean accept(Schedule schedule, Allocation allocation) {
        JobType jobType = allocation.getJob().getJobType();
        return jobType == JobType.SOURCE || jobType == JobType.SINK;
    }

}
