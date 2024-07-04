package org.example.task.solver;

import org.example.task.WorkCenter;

import java.util.Comparator;


public class WorkCenterStrengthWeightFactory implements Comparator<WorkCenter> {

    public static final Comparator<WorkCenter> COMPARATOR = Comparator.comparingInt(WorkCenter::getPriority);

    @Override
    public int compare(WorkCenter o1, WorkCenter o2) {
        return COMPARATOR.compare(o1, o2);
    }
}
