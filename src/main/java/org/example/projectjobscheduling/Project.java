package org.example.projectjobscheduling;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.*;
import org.example.projectjobscheduling.resource.LocalResource;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Project extends AbstractPersistable {

    /**
     * 项目开始时间，这里从0开始
     */
    private int releaseDate;

    /**
     * 关键路径耗时
     */
    private int criticalPathDuration;

    /**
     * 项目所需的本地资源
     */
    private List<LocalResource> localResourceList;

    /**
     * 项目中所有的任务
     */
    private List<Job> jobList;

    public Project() {
    }

    public Project(long id, int releaseDate, int criticalPathDuration) {
        super(id);
        this.releaseDate = releaseDate;
        this.criticalPathDuration = criticalPathDuration;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * 该项目的结束时间
     *
     * @return /
     */
    public int getCriticalPathEndDate() {
        return releaseDate + criticalPathDuration;
    }

}
