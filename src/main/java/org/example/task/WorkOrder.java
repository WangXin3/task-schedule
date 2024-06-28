package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * 项目开始时间
     */
    private Integer releaseDate;

    /**
     * 历时多少天之后交付，即交期-开始日期
     */
    private Integer criticalPathDuration;


    /**
     * 该派工单的所有工序任务
     */
    @EqualsAndHashCode.Exclude
    private List<Task> taskList;


    /**
     * 该项目的结束时间
     *
     * @return /
     */
    public int getCriticalPathEndDate() {
        return releaseDate + criticalPathDuration;
    }
}
