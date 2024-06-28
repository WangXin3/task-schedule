package org.example.projectjobscheduling;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Job extends AbstractPersistable {

    /**
     * 任务所属的项目
     */
    private Project project;

    /**
     * 任务类型
     */
    private JobType jobType;

    /**
     * 任务可选的执行模式
     */
    private List<ExecutionMode> executionModeList;

    /**
     * 后续任务列表
     */
    private List<Job> successorJobList;

    public Job() {
    }

    public Job(long id, Project project) {
        super(id);
        this.project = project;
    }

}
