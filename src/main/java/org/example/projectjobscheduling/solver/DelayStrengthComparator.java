package org.example.projectjobscheduling.solver;

import java.util.Comparator;

public class DelayStrengthComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer a, Integer b) {
        return a.compareTo(b);
    }

}
