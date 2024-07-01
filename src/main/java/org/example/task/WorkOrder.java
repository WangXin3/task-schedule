package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangxin
 * @since 2024/6/28 10:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrder {

    /**
     * 派工单名称
     */
    private String workOrderName;

    /**
     * 项目开始时间 取这一批最早的派工单的计划开始时间为基准0
     */
    private Integer releaseDate;

    /**
     * 项目期望持续多长时间完成（单位：小时）
     */
    private Integer criticalPathDuration;



    /**
     * 该项目的结束时间
     *
     * @return /
     */
    public int getCriticalPathEndDate() {
        return releaseDate + criticalPathDuration;
    }
}
