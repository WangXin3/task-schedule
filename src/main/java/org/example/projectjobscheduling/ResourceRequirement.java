package org.example.projectjobscheduling;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.*;
import org.example.projectjobscheduling.resource.Resource;

@Getter
@Setter
@AllArgsConstructor
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ResourceRequirement extends AbstractPersistable {

    /**
     * 执行模式
     */
    private ExecutionMode executionMode;

    /**
     * 资源
     */
    private Resource resource;

    /**
     * 需求量
     */
    private int requirement;

    public ResourceRequirement() {
    }

    public ResourceRequirement(long id, ExecutionMode executionMode, Resource resource, int requirement) {
        super(id);
        this.executionMode = executionMode;
        this.resource = resource;
        this.requirement = requirement;
    }


    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public boolean isResourceRenewable() {
        return resource.isRenewable();
    }

}
