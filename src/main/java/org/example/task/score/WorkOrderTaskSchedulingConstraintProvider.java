package org.example.task.score;

import org.example.projectjobscheduling.JobType;
import org.example.task.Plan;
import org.example.task.Task;
import org.example.task.WorkCenter;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkOrderTaskSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                /* hard */
                renewableResourceCapacity(constraintFactory),
                workCenter(constraintFactory),

                /* MEDIUM */
                totalProjectDelay(constraintFactory),

                /* SOFT */
                totalMakespan(constraintFactory),
                sortWorkCenter(constraintFactory)
        };
    }




    Constraint workCenter(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .filter(plan -> !plan.getWorkCenter()
                        .getTaskTypes().contains(plan.getTask().getTaskType()))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("工作中心不匹配");
    }


    protected Constraint renewableResourceCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(WorkCenter.class)
                .join(Plan.class, Joiners.equal(Function.identity(), Plan::getWorkCenter))
                .flattenLast(plan -> IntStream.range(plan.getStartDate(), plan.getEndDate())
                        .boxed()
                        .collect(Collectors.toList()))
                .groupBy((workCenter, date) -> workCenter, (workCenter, date) -> date,
                        ConstraintCollectors.sum((workCenter, date) -> 1))
                .filter((workCenter, date, totalRequirement) -> totalRequirement > workCenter.getCapacity())
                .penalize(HardMediumSoftScore.ONE_HARD,
                        (workCenter, date, totalRequirement) -> totalRequirement - workCenter.getCapacity())
                .asConstraint("Renewable resource capacity");
    }

    protected Constraint totalProjectDelay(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .filter(plan -> plan.getEndDate() != null)
                .filter(plan -> plan.getJobType() == JobType.SINK)
                .impact(HardMediumSoftScore.ONE_MEDIUM,
                        plan -> plan.getProjectCriticalPathEndDate() - plan.getEndDate())
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

    private Constraint sortWorkCenter(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Plan.class)
                .penalize(HardMediumSoftScore.ONE_SOFT, value ->  {
                    Task task = value.getTask();
                    // 当前选中的工作中心
                    WorkCenter workCenter = value.getWorkCenter();
                    Integer currPriority = workCenter.getPriority();

                    List<WorkCenter> workCenterList = task.getWorkCenterList();
                    long count = workCenterList.stream()
                            .filter(w -> !w.equals(workCenter))
                            .filter(w -> w.getPriority() < currPriority)
                            .count();

                    return Math.toIntExact(count);
                })
                .asConstraint("优先级问题");
    }
}
