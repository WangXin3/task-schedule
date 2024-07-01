package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import java.util.List;

@PlanningSolution
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskSchedule {

    @ProblemFactCollectionProperty
    private List<WorkOrder> workOrderList;

    @ProblemFactCollectionProperty
    private List<Task> taskList;

    @ProblemFactCollectionProperty
    private List<WorkCenter> workCenterList;

    @PlanningEntityCollectionProperty
    private List<Plan> planList;

    @PlanningScore
    private HardMediumSoftScore score;
}


