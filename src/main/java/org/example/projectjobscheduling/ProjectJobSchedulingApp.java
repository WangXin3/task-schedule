package org.example.projectjobscheduling;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectjobscheduling.resource.Resource;
import org.example.projectjobscheduling.score.ProjectJobSchedulingConstraintProvider;
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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class ProjectJobSchedulingApp {

    public static void main(String[] args) {
        SolverConfig solverConfig = new SolverConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setAcceptorConfig(new LocalSearchAcceptorConfig()
                .withEntityTabuRatio(0.2).withLateAcceptanceSize(500));
        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig().withAcceptedCountLimit(4));
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig().withMoveSelectors(
                new ChangeMoveSelectorConfig().withValueSelectorConfig(new ValueSelectorConfig("executionMode")),
                new ChangeMoveSelectorConfig().withValueSelectorConfig(new ValueSelectorConfig("delay"))));

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);

        solverConfig.withSolutionClass(Schedule.class)
                .withEntityClasses(Allocation.class)
                .withConstraintProviderClass(ProjectJobSchedulingConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
                .withPhaseList(List.of(constructionHeuristicPhaseConfig, localSearchPhaseConfig));

        SolverFactory<Schedule> solverFactory = SolverFactory.create(solverConfig);

        Solver<Schedule> solver = solverFactory.buildSolver();

        Schedule unsolvedSchedule = initData();

        Schedule solvedSchedule = solver.solve(unsolvedSchedule);

        Map<Resource, Integer> useMap = new HashMap<>();

        for (Allocation allocation : solvedSchedule.getAllocationList()) {
            Long id = allocation.getId();
            Integer startDate = allocation.getStartDate();
            Integer endDate = allocation.getEndDate();
            System.out.println("id:" + id + ", " + startDate + " - " + endDate);

            List<ResourceRequirement> resourceRequirements = allocation.getExecutionMode().getResourceRequirementList();
            for (ResourceRequirement resourceRequirement : resourceRequirements) {
                Resource resource = resourceRequirement.getResource();
                int requirement = resourceRequirement.getRequirement();
                Integer orDefault = useMap.getOrDefault(resource, 0);
                orDefault += requirement;
                useMap.put(resource, orDefault);
            }

        }

        System.out.println(solvedSchedule);


    }

    private static Schedule initData() {
        String s = FileUtil.readString("D:\\Code\\task-schedule\\src\\main\\java\\org\\example\\projectjobscheduling\\data\\A-1.json", StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, Schedule.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
