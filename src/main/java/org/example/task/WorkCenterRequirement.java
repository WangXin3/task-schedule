package org.example.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectjobscheduling.resource.Resource;

/**
 * @author wangxin
 * @since 2024/6/28 14:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkCenterRequirement {

    /**
     * 资源
     */
    private WorkCenter workCenter;

    /**
     * 需求量 小时
     */
    private Integer requirement;
}
