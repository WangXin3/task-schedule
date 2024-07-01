package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkCenter {
    /**
     * 工作中心名称
     */
    private String workCenterName;

    /**
     * 该工作中心可以干的工序类型列表
     */
    private List<TaskType> taskTypes;

    /**
     * 优先级 1-10，越小越靠前
     */
    private Integer priority;

    /**
     * 该工作中心在一个时间单元内能干几个工序任务
     */
    private Integer capacity;


}