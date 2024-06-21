package org.example.task;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TaskScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredWorkCenter(constraintFactory),
                workCenterPriority(constraintFactory)
        };
    }

    private Constraint workCenterPriority(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(WorkCenter.class)
                .penalize(HardSoftScore.ONE_SOFT, workCenter -> workCenter.getPriority() - 1)
                .asConstraint("工作中心优先级");
    }


    Constraint requiredWorkCenter(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(WorkCenter.class)
                .filter(workCenter -> !workCenter.getTaskList().stream()
                        .allMatch(w -> workCenter.getTaskTypes().contains(w.getType())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("工作中心需匹配");
    }


}


