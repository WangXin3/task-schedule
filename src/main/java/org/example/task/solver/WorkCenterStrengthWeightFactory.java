package org.example.task.solver;

import org.example.task.TaskSchedule;
import org.example.task.WorkCenter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.util.Comparator;


public class WorkCenterStrengthWeightFactory implements SelectionSorterWeightFactory<TaskSchedule, WorkCenter> {

    @Override
    public WorkCenterStrengthWeight createSorterWeight(TaskSchedule taskSchedule, WorkCenter workCenter) {
        return new WorkCenterStrengthWeight(workCenter.getPriority());
    }

    public static class WorkCenterStrengthWeight implements Comparable<WorkCenterStrengthWeight> {

        private static final Comparator<WorkCenterStrengthWeight> COMPARATOR =
                Comparator.comparingInt((WorkCenterStrengthWeight weight) -> weight.priority);

        private final Integer priority;

        public WorkCenterStrengthWeight(Integer priority) {
            this.priority = priority;
        }

        @Override
        public int compareTo(WorkCenterStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
