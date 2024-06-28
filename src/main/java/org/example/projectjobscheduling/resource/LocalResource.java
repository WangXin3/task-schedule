package org.example.projectjobscheduling.resource;


import lombok.*;
import org.example.projectjobscheduling.Project;

@Getter
@Setter
@AllArgsConstructor
public class LocalResource extends Resource {

    /**
     * 所属项目
     */
    private Project project;

    /**
     * 是否可再生
     */
    private boolean renewable;

    public LocalResource() {
    }

    public LocalResource(long id, Project project, boolean renewable) {
        super(id, 0);
        this.project = project;
        this.renewable = renewable;
    }
}
