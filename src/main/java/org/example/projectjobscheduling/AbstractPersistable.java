package org.example.projectjobscheduling;

import lombok.*;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPersistable {

    @PlanningId
    protected Long id;

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
