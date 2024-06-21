package org.example.queen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 规划实体类注解
 */
@PlanningEntity
public class Queen {

    @PlanningId
    private Integer id;
    @PlanningVariable
    private Column column;
    @PlanningVariable
    private Row row;

    /**
     * 升序对角线索引（左上到右下）
     *
     * @return /
     */
    public int getAscendingDiagonalIndex() {
        return column.getIndex() + row.getIndex();
    }

    /**
     * 降序对角线索引（左下到右上）
     *
     * @return /
     */
    public int getDescendingDiagonalIndex() {
        return column.getIndex() - row.getIndex();
    }
}