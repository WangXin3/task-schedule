package org.example.projectjobscheduling;

import lombok.*;
import org.example.projectjobscheduling.resource.Resource;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

import java.util.List;

@PlanningSolution
@Getter
@Setter
@AllArgsConstructor
public class Schedule extends AbstractPersistable {

    @ProblemFactCollectionProperty
    private List<Project> projectList;

    @ProblemFactCollectionProperty
    private List<Job> jobList;

    @ProblemFactCollectionProperty
    private List<ExecutionMode> executionModeList;

    @ProblemFactCollectionProperty
    private List<Resource> resourceList;

    @ProblemFactCollectionProperty
    private List<ResourceRequirement> resourceRequirementList;

    @PlanningEntityCollectionProperty
    private List<Allocation> allocationList;

    @PlanningScore
    private HardMediumSoftScore score;

    public Schedule() {
    }

    public Schedule(long id) {
        super(id);
    }



}
