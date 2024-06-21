package org.example.queen;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@PlanningSolution
public class NQueen {
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Column> columnList;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Row> rowList;

    @PlanningEntityCollectionProperty
    private List<Queen> queenList;

    @PlanningScore
    private HardSoftScore score;
}