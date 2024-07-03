package org.example.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import org.example.projectjobscheduling.JobType;
import org.example.task.score.WorkOrderTaskSchedulingConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangxin
 * @since 2024/6/11 15:08
 */

public class TaskScheduleApp {
    private static final List<WorkCenter> WORK_CENTER_LIST;
    private static final List<Process> PROCESS_LIST;

    private static final List<WorkOrder> WORK_ORDER_LIST;

    static {
        WorkCenter workCenter1 = new WorkCenter("WorkC1", List.of(
                TaskType.A1, TaskType.B2, TaskType.C3, TaskType.A4), 3, 1);
        WorkCenter workCenter2 = new WorkCenter("WorkC2", List.of(
                TaskType.A2, TaskType.B1, TaskType.C1, TaskType.B4), 1, 1);
        WorkCenter workCenter3 = new WorkCenter("WorkC3", List.of(
                TaskType.A3, TaskType.B3, TaskType.C2), 1, 1);
        WorkCenter workCenter4 = new WorkCenter("WorkC4", List.of(
                TaskType.A4, TaskType.B4, TaskType.C4, TaskType.A1, TaskType.B3), 2, 1);
        WORK_CENTER_LIST = List.of(workCenter1, workCenter2, workCenter3, workCenter4);


        WorkOrder workOrder1 = new WorkOrder("工单一", 0, 100, 9);
        WorkOrder workOrder2 = new WorkOrder("工单二", 5, 100, 7);
        WorkOrder workOrder3 = new WorkOrder("工单三", 3, 100, 8);
        WORK_ORDER_LIST = List.of(workOrder1, workOrder2, workOrder3);

        Process C4 = new Process("1", workOrder1, TaskType.C4.name(), TaskType.C4, JobType.SINK, 1, null);
        Process A3 = new Process("2", workOrder1, TaskType.A3.name(), TaskType.A3, JobType.STANDARD, 1, List.of(C4));
        Process B1 = new Process("3", workOrder1, TaskType.B1.name(), TaskType.B1, JobType.STANDARD, 2, List.of(C4));
//         B2 -> A3 -> C4
//          ↘-→ B1 -→↗
        Process B2 = new Process("4", workOrder1, TaskType.B2.name(), TaskType.B2, JobType.SOURCE, 3, List.of(A3, B1));

        C4.setPrevProcessList(List.of(A3, B1));
        A3.setPrevProcessList(List.of(B2));
        B1.setPrevProcessList(List.of(B2));

        Process A4 = new Process("5", workOrder2, TaskType.A4.name(), TaskType.A4, JobType.SINK, 3, null);
        Process A2 = new Process("6", workOrder2, TaskType.A2.name(), TaskType.A2, JobType.STANDARD, 1, List.of(A4));
        Process B3 = new Process("7", workOrder2, TaskType.B3.name(), TaskType.B3, JobType.STANDARD, 2, List.of(A2));
        Process C1 = new Process("8", workOrder2, TaskType.C1.name(), TaskType.C1, JobType.STANDARD, 4, List.of(B3));
        Process C2 = new Process("9", workOrder2, TaskType.C2.name(), TaskType.C2, JobType.STANDARD, 1, List.of(C1));
        // B4 -> C2 -> C1 -> B3 -> A2 -> A4
        Process B4 = new Process("10", workOrder2, TaskType.B4.name(), TaskType.B4, JobType.SOURCE, 3, List.of(C2));

        A4.setPrevProcessList(List.of(A2));
        A2.setPrevProcessList(List.of(B3));
        B3.setPrevProcessList(List.of(C1));
        C1.setPrevProcessList(List.of(C2));
        C2.setPrevProcessList(List.of(B4));


        Process C3 = new Process("11", workOrder3, TaskType.C3.name(), TaskType.C3, JobType.SINK, 3, null);
        // A1 -> C3
        Process A1 = new Process("12", workOrder3, TaskType.A1.name(), TaskType.A1, JobType.SOURCE, 1, List.of(C3));

        C3.setPrevProcessList(List.of(A1));

        PROCESS_LIST = List.of(B2, B4, A1);
//        PROCESS_LIST = List.of(B2);
    }

    public static List<WorkCenter> getWorkCenterListByWorkType(TaskType taskType) {
        return WORK_CENTER_LIST.stream().filter(w -> w.getTaskTypes().contains(taskType))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        TaskScheduleApp taskScheduleApp = new TaskScheduleApp();
        taskScheduleApp.test2();
    }

    void test2() {
        SolverConfig solverConfig = new SolverConfig();

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setAcceptorConfig(new LocalSearchAcceptorConfig()
                .withEntityTabuRatio(0.2).withLateAcceptanceSize(500));

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig().withAcceptedCountLimit(4));

        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig().withMoveSelectors(
                new ChangeMoveSelectorConfig()
                        .withValueSelectorConfig(new ValueSelectorConfig("workCenter")),
                new ChangeMoveSelectorConfig()
                        .withValueSelectorConfig(new ValueSelectorConfig("delay"))));

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);

        solverConfig.withSolutionClass(TaskSchedule.class)
                .withEntityClasses(Plan.class)
                .withConstraintProviderClass(WorkOrderTaskSchedulingConstraintProvider.class)
                .withTerminationConfig(new TerminationConfig().withUnimprovedSecondsSpentLimit(5L))
//                .withTerminationSpentLimit(Duration.ofSeconds(5))
                .withPhaseList(List.of(constructionHeuristicPhaseConfig, localSearchPhaseConfig));

        SolverFactory<TaskSchedule> solverFactory = SolverFactory.create(solverConfig);
        Solver<TaskSchedule> solver = solverFactory.buildSolver();


        TaskSchedule unsolvedTaskSchedule = initializeTaskSchedule();

        TaskSchedule solvedTaskSchedule = solver.solve(unsolvedTaskSchedule);

        System.out.println("Solved Task Schedule:");
        System.out.println();

        Map<WorkOrder, List<Plan>> map = solvedTaskSchedule.getPlanList()
                .stream().collect(Collectors.groupingBy(Plan::getWorkOrder));
        for (Map.Entry<WorkOrder, List<Plan>> entry : map.entrySet()) {
            WorkOrder workOrder = entry.getKey();
            List<Plan> planList = entry.getValue();
            planList.sort(Comparator.comparingInt(Plan::getStartDate));

//            System.out.println("workOrder: " + workOrder.getWorkOrderName()
//                    + " startTime: " + workOrder.getReleaseDate());

            planList.forEach(p -> {
                Task task = p.getTask();
                WorkCenter workCenter = p.getWorkCenter();
//                System.out.println(task.getTaskName() + "(" + task.getDuration() + ") "
//                        + workCenter.getWorkCenterName() + "(" + workCenter.getPriority() + ")" + workCenter.getTaskTypes()
//                        + " --> startDate: " + p.getStartDate() + " endDate: " + p.getEndDate());

                System.out.println(workOrder.getWorkOrderName() + workOrder.getReleaseDate()
                        + "," + task.getTaskName() + "," + workCenter.getWorkCenterName()
                        + "," + p.getStartDate() + "," + p.getEndDate() + "||"
                        + p.getPredecessorPlanList().stream().map(pp -> pp.getTask().getTaskName())
                        .collect(Collectors.joining(",")));

            });
        }
    }

    private TaskSchedule initializeTaskSchedule() {
        List<Task> taskList = PROCESS_LIST.stream().flatMap(p -> {
            WorkOrder workOrder = p.getWorkOrder();
            if (workOrder.getNum() == null) {
                workOrder.setNum(RandomUtil.randomInt(1, 10));
            }

            // 生成任务
            List<Task> taskListTemp = this.createTask(p, workOrder.getNum());
            return taskListTemp.stream();
        }).collect(Collectors.toList());

        // 生成计划
        List<Plan> planList = new ArrayList<>(taskList.size());
        Map<Task, Plan> taskToPlanMap = new HashMap<>(taskList.size());

        Set<WorkCenter> workCenterList = new HashSet<>();
        for (Task task : taskList) {
            workCenterList.addAll(task.getWorkCenterList());
            Plan plan = new Plan();
            plan.setTask(task);
            plan.setPredecessorPlanList(new ArrayList<>(task.getNextTaskList().size()));
            plan.setSuccessorPlanList(new ArrayList<>(task.getNextTaskList().size()));

            // Uninitialized allocations take no time, but don't break the predecessorsDoneDate cascade to sink.
            plan.setPredecessorsDoneDate(task.getWorkOrder().getReleaseDate());

            planList.add(plan);
            taskToPlanMap.put(task, plan);
        }

        // 构建计划的前置/后置关系
        for (Plan plan : planList) {
            Task task = plan.getTask();

            for (Task nextTask : task.getNextTaskList()) {
                // 将本计划的下一计划查询出来
                Plan successorPlan = taskToPlanMap.get(nextTask);
                // 将本计划的下一计划添加到后置计划列表中
                plan.getSuccessorPlanList().add(successorPlan);
                // 将本计划添加到下一计划的前置计划列表中
                successorPlan.getPredecessorPlanList().add(plan);
            }
        }

        return new TaskSchedule(WORK_ORDER_LIST, taskList, new ArrayList<>(workCenterList),
                planList, null);
    }


    private List<Task> createTask(Process process, Integer num) {
        Set<Task> taskList = new HashSet<>();

        Map<String, Task> taskMap = new HashMap<>();
        Task task = this.extracted(process, num, taskMap);

        return new ArrayList<>(this.getTaskList(taskList, task));
    }

    private Set<Task> getTaskList(Set<Task> taskList, Task task) {
        taskList.add(task);

        if (CollUtil.isNotEmpty(task.getNextTaskList())) {
            for (Task nextTask : task.getNextTaskList()) {
                getTaskList(taskList, nextTask);
            }

        }

        return taskList;
    }

    private Task extracted(Process process, Integer num, Map<String, Task> taskMap) {
        if (taskMap.containsKey(process.getId())) {
            return taskMap.get(process.getId());
        }

        // 生成任务
        Task currTask = new Task(process.getWorkOrder(), process.getProcessName(),
                process.getDuration() * num, new ArrayList<>(), process.getTaskType(),
                process.getJobType(), getWorkCenterListByWorkType(process.getTaskType()));
        taskMap.put(process.getId(), currTask);

        if (CollUtil.isNotEmpty(process.getNextProcessList())) {

            for (Process nextProcess : process.getNextProcessList()) {
                Task nextTask = this.extracted(nextProcess, num, taskMap);
                currTask.getNextTaskList().add(nextTask);
            }
        }

        return currTask;
    }
}


