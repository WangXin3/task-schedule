package org.example.task.solver;

import org.example.projectjobscheduling.Allocation;
import org.example.projectjobscheduling.Schedule;
import org.example.task.Plan;
import org.example.task.TaskSchedule;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class PredecessorsDoneDateUpdatingVariableListener implements VariableListener<TaskSchedule, Plan> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        updateAllocation(scoreDirector, plan);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        updateAllocation(scoreDirector, plan);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        // Do nothing
    }

    protected void updateAllocation(ScoreDirector<TaskSchedule> scoreDirector, Plan originalPlan) {
        Queue<Plan> uncheckedSuccessorQueue = new ArrayDeque<>(originalPlan.getSuccessorPlanList());
        while (!uncheckedSuccessorQueue.isEmpty()) {
            Plan plan = uncheckedSuccessorQueue.remove();
            boolean updated = updatePredecessorsDoneDate(scoreDirector, plan);
            if (updated) {
                uncheckedSuccessorQueue.addAll(plan.getSuccessorPlanList());
            }
        }
    }

    /**
     * @param scoreDirector never null
     * @param plan never null
     * @return true if the startDate changed
     */
    protected boolean updatePredecessorsDoneDate(ScoreDirector<TaskSchedule> scoreDirector, Plan plan) {
        // For the source the doneDate must be 0.
        int doneDate = 0;
        for (Plan predecessorPlan : plan.getPredecessorPlanList()) {
            int endDate = predecessorPlan.getEndDate();
            doneDate = Math.max(doneDate, endDate);
        }
        if (Objects.equals(doneDate, plan.getPredecessorsDoneDate())) {
            return false;
        }
        scoreDirector.beforeVariableChanged(plan, "predecessorsDoneDate");
        plan.setPredecessorsDoneDate(doneDate);
        scoreDirector.afterVariableChanged(plan, "predecessorsDoneDate");
        return true;
    }

}
