package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 工序节点
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Process {
    /**
     * 工艺路线名称
     */
    private String processRouteName;

    /**
     * 工序名称
     */
    private String processName;

    /**
     * 工序类型
     */
    private TaskType taskType;

    /**
     * 工序单件耗时
     */
    private Integer duration;

    /**
     * 后置工序
     */
    private List<Process> nextProcess;
}