package org.example.task;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public class DeadLineVariableListener implements VariableListener<TaskSchedule, Task> {

    @Override
    public void beforeVariableChanged(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 参数改变之前
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 参数改变之后
        updateVehicle(scoreDirector, task);
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 添加实体之前
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 添加实体之后
        updateVehicle(scoreDirector, task);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 实体删除之前
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        // 实体删除之后
    }

    protected void updateVehicle(ScoreDirector<TaskSchedule> scoreDirector, Task task) {
        List<Task> prevTaskList = task.getPrevTaskList();
        if (CollUtil.isNotEmpty(prevTaskList)) {
            Task maxTask = prevTaskList.stream().max(Comparator.comparing(Task::getDeadLine)).orElse(null);
            if (maxTask != null) {
                task.setDeadLine(maxTask.getDeadLine() + (int) task.getDuration().toHours());
            } else {
                task.setDeadLine((int) task.getDuration().toHours());
            }
        } else {
            task.setDeadLine((int) task.getDuration().toHours());
        }

    }
}


