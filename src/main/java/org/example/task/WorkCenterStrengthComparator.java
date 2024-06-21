package org.example.task;

import java.util.Comparator;

/**
 * 求解workCenter时优先级问题
 *
 * @author wangxin
 * @since 2024/6/21 16:42
 */
public class WorkCenterStrengthComparator implements Comparator<WorkCenter> {

    private static final Comparator<WorkCenter> COMPARATOR =
            Comparator.comparing(WorkCenter::getPriority);

    @Override
    public int compare(WorkCenter o1, WorkCenter o2) {
        return COMPARATOR.compare(o1, o2);
    }
}
