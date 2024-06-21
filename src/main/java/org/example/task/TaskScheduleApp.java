package org.example.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.time.Duration;
import java.util.*;

/**
 * @author wangxin
 * @since 2024/6/11 15:08
 */

public class TaskScheduleApp {
    private static final List<WorkCenter> WORK_CENTER_LIST;
    private static final List<Process> PROCESS_LIST;

    static {
        WorkCenter workCenter1 = new WorkCenter("WorkCenter1", List.of(TaskType.A1,
                TaskType.B2, TaskType.C3, TaskType.A4), 1, Collections.emptyList());
        WorkCenter workCenter2 = new WorkCenter("WorkCenter2", List.of(TaskType.A2,
                TaskType.B1, TaskType.C1, TaskType.B4), 1, Collections.emptyList());
        WorkCenter workCenter3 = new WorkCenter("WorkCenter3", List.of(TaskType.A3,
                TaskType.B3, TaskType.C2), 2, Collections.emptyList());
        WorkCenter workCenter4 = new WorkCenter("WorkCenter4", List.of(TaskType.A4,
                TaskType.B4, TaskType.C4, TaskType.A1, TaskType.B3), 1, Collections.emptyList());
        WORK_CENTER_LIST = List.of(workCenter1, workCenter2, workCenter3, workCenter4);


        Process C4 = new Process("WorkOrder1", TaskType.C4.name(), TaskType.C4, 1, null);
        Process A3 = new Process("WorkOrder1", TaskType.A3.name(), TaskType.A3, 1, List.of(C4));
        Process B1 = new Process("WorkOrder1", TaskType.B1.name(), TaskType.B1, 2, List.of(C4));
        // B2 -> A3 -> C4
        //  ↘-→ B1 -→↗
        Process B2 = new Process("WorkOrder1", TaskType.B2.name(), TaskType.B2, 3, List.of(A3, B1));

        Process A4 = new Process("WorkOrder2", TaskType.A4.name(), TaskType.A4, 3, null);
        Process A2 = new Process("WorkOrder2", TaskType.A2.name(), TaskType.A2, 1, List.of(A4));
        Process B3 = new Process("WorkOrder2", TaskType.B3.name(), TaskType.B3, 2, List.of(A2));
        // C1 -> B3 -> A2 -> A4
        Process C1 = new Process("WorkOrder2", TaskType.C1.name(), TaskType.C1, 4, List.of(B3));

        Process C3 = new Process("WorkOrder3", TaskType.C3.name(), TaskType.C3, 3, null);
        // A1 -> C3
        Process A1 = new Process("WorkOrder3", TaskType.A1.name(), TaskType.A1, 1, List.of(C3));


        PROCESS_LIST = List.of(B2, C1, A1);
//        PROCESS_LIST = List.of(B2);
    }

    public static void main(String[] args) {
        TaskScheduleApp taskScheduleApp = new TaskScheduleApp();
        taskScheduleApp.test2();
    }

    void test2() {
        SolverFactory<TaskSchedule> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TaskSchedule.class)
                .withEntityClasses(WorkCenter.class)
                .withConstraintProviderClass(TaskScheduleConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        );

        Solver<TaskSchedule> solver = solverFactory.buildSolver();

        TaskSchedule unsolvedTaskSchedule = initializeTaskSchedule();

        TaskSchedule solvedTaskSchedule = solver.solve(unsolvedTaskSchedule);

        System.out.println("Solved Task Schedule:");
        for (Task task : solvedTaskSchedule.getTaskList()) {
//            WorkCenter workCenter = task.getWorkCenter();
            System.out.println("Task " + task.getTaskName()
                    + "(" + task.getDuration().toHours() + "), DeadLine (" + task.getDeadLine() + ") at ");
//                    + workCenter.getWorkCenterName() + "(" + workCenter.getPriority() + ")"
//                    + workCenter.getTaskTypes());
        }
    }

    private TaskSchedule initializeTaskSchedule() {


        Process Process = PROCESS_LIST.get(1);
        Map<Process, Task> arranged = new HashMap<>();
        // 生成任务
        List<Task> taskList = this.createTask(Process, arranged, RandomUtil.randomInt(1, 20),
                null, 0);

        taskList.sort(Comparator.comparing(Task::getDeep));

        return new TaskSchedule(WORK_CENTER_LIST, taskList, null);
    }


    private List<Task> createTask(Process process, Map<Process, Task> arranged, Integer num, List<Task> prevTaskList,
                                  Integer deep) {
        if (arranged.containsKey(process)) {
            Task task = arranged.get(process);
            List<Task> prevTaskListTemp = task.getPrevTaskList();
            prevTaskListTemp.addAll(prevTaskList);
            return Collections.emptyList();
        }

        List<Task> taskList = new ArrayList<>();

        // 生成任务
        Task currTask = new Task(process.getProcessName(), Duration.ofHours((long) process.getDuration() * num),
                prevTaskList, process.getTaskType(), deep, null);
        // 当前工序
        arranged.put(process, currTask);
        taskList.add(currTask);

        if (CollUtil.isNotEmpty(process.getNextProcess())) {
            List<Task> prevTaskListTemp = new ArrayList<>();
            prevTaskListTemp.add(currTask);
            for (Process nextProcess : process.getNextProcess()) {
                taskList.addAll(this.createTask(nextProcess, arranged, num, prevTaskListTemp, deep + 1));
            }
        }

        return taskList;
    }
}


