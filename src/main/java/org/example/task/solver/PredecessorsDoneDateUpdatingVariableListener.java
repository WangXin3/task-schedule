package org.example.task.solver;

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
        // 获取当前计划的所有后置计划
        Queue<Plan> uncheckedSuccessorQueue = new ArrayDeque<>(originalPlan.getSuccessorPlanList());
        while (!uncheckedSuccessorQueue.isEmpty()) {
            // 取出一个
            Plan plan = uncheckedSuccessorQueue.remove();
            // 去更新
            boolean updated = updatePredecessorsDoneDate(scoreDirector, plan);
            if (updated) {
                // 如果更新了当前计划的开始时间（即当前计划的前置计划的最晚结束时间），则需要更新本计划所有后续计划的开始时间。
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
        // 取出所有的前置计划并取出最晚的结束时间
        for (Plan predecessorPlan : plan.getPredecessorPlanList()) {
            int endDate = predecessorPlan.getEndDate();
            doneDate = Math.max(doneDate, endDate);
        }
        // 如果一致，则不更新
        if (Objects.equals(doneDate, plan.getPredecessorsDoneDate())) {
            return false;
        }
        // 否则更新当前计划的前置计划结束时间
        scoreDirector.beforeVariableChanged(plan, "predecessorsDoneDate");
        plan.setPredecessorsDoneDate(doneDate);
        scoreDirector.afterVariableChanged(plan, "predecessorsDoneDate");
        return true;
    }

}
