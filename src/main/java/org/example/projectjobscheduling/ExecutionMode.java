package org.example.projectjobscheduling;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ExecutionMode extends AbstractPersistable {

    /**
     * 任务
     */
    private Job job;

    /**
     * 耗时
     */
    private int duration; // In days

    /**
     * 所需资源列表
     */
    private List<ResourceRequirement> resourceRequirementList;

    public ExecutionMode() {
    }

    public ExecutionMode(long id, Job job) {
        super(id);
        this.job = job;
    }

}
