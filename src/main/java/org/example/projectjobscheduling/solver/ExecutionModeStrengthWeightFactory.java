package org.example.projectjobscheduling.solver;

import org.example.projectjobscheduling.ExecutionMode;
import org.example.projectjobscheduling.ResourceRequirement;
import org.example.projectjobscheduling.Schedule;
import org.example.projectjobscheduling.resource.Resource;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

public class ExecutionModeStrengthWeightFactory implements SelectionSorterWeightFactory<Schedule, ExecutionMode> {

    @Override
    public ExecutionModeStrengthWeight createSorterWeight(Schedule schedule, ExecutionMode executionMode) {
        Map<Resource, Integer> requirementTotalMap = new HashMap<>(
                executionMode.getResourceRequirementList().size());
        for (ResourceRequirement resourceRequirement : executionMode.getResourceRequirementList()) {
            requirementTotalMap.put(resourceRequirement.getResource(), 0);
        }
        for (ResourceRequirement resourceRequirement : schedule.getResourceRequirementList()) {
            Resource resource = resourceRequirement.getResource();
            Integer total = requirementTotalMap.get(resource);
            if (total != null) {
                total += resourceRequirement.getRequirement();
                requirementTotalMap.put(resource, total);
            }
        }
        double requirementDesirability = 0.0;
        for (ResourceRequirement resourceRequirement : executionMode.getResourceRequirementList()) {
            Resource resource = resourceRequirement.getResource();
            int total = requirementTotalMap.get(resource);
            if (total > resource.getCapacity()) {
                requirementDesirability += (total - resource.getCapacity())
                        * (double) resourceRequirement.getRequirement()
                        * (resource.isRenewable() ? 1.0 : 100.0);
            }
        }
        return new ExecutionModeStrengthWeight(executionMode, requirementDesirability);
    }

    public static class ExecutionModeStrengthWeight implements Comparable<ExecutionModeStrengthWeight> {

        private static final Comparator<ExecutionModeStrengthWeight> COMPARATOR = comparingDouble(
                (ExecutionModeStrengthWeight weight) -> weight.requirementDesirability)
                .thenComparing(weight -> weight.executionMode, comparingLong(ExecutionMode::getId));

        private final ExecutionMode executionMode;
        private final double requirementDesirability;

        public ExecutionModeStrengthWeight(ExecutionMode executionMode, double requirementDesirability) {
            this.executionMode = executionMode;
            this.requirementDesirability = requirementDesirability;
        }

        @Override
        public int compareTo(ExecutionModeStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
