package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskSchedule {

    @PlanningEntityCollectionProperty
    private List<WorkCenter> workCenterList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Task> taskList;

    @PlanningScore
    private HardSoftScore score;
}


