package org.example.task.solver;

import org.example.task.TaskSchedule;
import org.example.task.WorkCenter;
import org.example.task.WorkCenterRequirement;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.util.Comparator;


public class WorkCenterStrengthWeightFactory implements SelectionSorterWeightFactory<TaskSchedule, WorkCenterRequirement> {

    @Override
    public WorkCenterStrengthWeight createSorterWeight(TaskSchedule taskSchedule, WorkCenterRequirement workCenterRequirement) {
        return new WorkCenterStrengthWeight(workCenterRequirement, workCenterRequirement.getWorkCenter().getPriority());
    }

    public static class WorkCenterStrengthWeight implements Comparable<WorkCenterStrengthWeight> {

        private static final Comparator<WorkCenterStrengthWeight> COMPARATOR =
                Comparator.comparingInt((WorkCenterStrengthWeight weight) -> weight.priority)
                        .thenComparing(weight -> weight.workCenterRequirement.getWorkCenter(),
                        Comparator.comparingInt(WorkCenter::getCapacity));

        private final WorkCenterRequirement workCenterRequirement;
        private final Integer priority;

        public WorkCenterStrengthWeight(WorkCenterRequirement workCenterRequirement, Integer priority) {
            this.workCenterRequirement = workCenterRequirement;
            this.priority = priority;
        }

        @Override
        public int compareTo(WorkCenterStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
