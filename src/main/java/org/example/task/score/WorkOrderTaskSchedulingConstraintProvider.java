package org.example.task.score;

import org.example.projectjobscheduling.Allocation;
import org.example.projectjobscheduling.JobType;
import org.example.projectjobscheduling.ResourceRequirement;
import org.example.task.Plan;
import org.example.task.Task;
import org.example.task.WorkCenter;
import org.example.task.WorkCenterRequirement;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkOrderTaskSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                renewableResourceCapacity(constraintFactory),
                workCenter(constraintFactory),
                totalProjectDelay(constraintFactory),
                totalMakespan(constraintFactory)
        };
    }

    Constraint workCenter(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .filter(plan -> !plan.getWorkCenterRequirement().getWorkCenter()
                        .getTaskTypes().contains(plan.getTask().getTaskType()))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("工作中心不匹配");
    }


    protected Constraint renewableResourceCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(WorkCenterRequirement.class)
                .join(Plan.class, Joiners.equal(Function.identity(), Plan::getWorkCenterRequirement))
                .flattenLast(plan -> IntStream.range(plan.getStartDate(), plan.getEndDate())
                        .boxed()
                        .collect(Collectors.toList()))
                .groupBy((workCenterRequirement, date) -> workCenterRequirement,
                        (workCenterRequirement, date) -> date,
                        ConstraintCollectors.sum((workCenterRequirement, date) -> workCenterRequirement.getRequirement()))
                .filter((workCenterRequirement, date, totalRequirement)
                        -> totalRequirement > workCenterRequirement.getWorkCenter().getCapacity())
                .penalize(HardMediumSoftScore.ONE_HARD,
                        (workCenterRequirement, date, totalRequirement)
                                -> totalRequirement - workCenterRequirement.getWorkCenter().getCapacity())
                .asConstraint("Renewable resource capacity");
    }

    protected Constraint totalProjectDelay(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .filter(plan -> plan.getEndDate() != null)
                .filter(plan -> plan.getJobType() == JobType.SINK)
                .impact(HardMediumSoftScore.ONE_MEDIUM,
                        allocation -> allocation.getProjectCriticalPathEndDate() - allocation.getEndDate())
                .asConstraint("Total project delay");
    }

    protected Constraint totalMakespan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .filter(plan -> plan.getEndDate() != null)
                .filter(plan -> plan.getJobType() == JobType.SINK)
                .groupBy(ConstraintCollectors.max(Plan::getEndDate))
                .penalize(HardMediumSoftScore.ONE_SOFT, maxEndDate -> maxEndDate)
                .asConstraint("Total makespan");
    }

}
