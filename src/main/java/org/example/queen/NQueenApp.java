package org.example.queen;

import org.example.queen.NQueen;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NQueenApp {

    public static void main(String[] args) {
        NQueenApp nQueenApp = new NQueenApp();
        nQueenApp.testNQueenApp();
    }

    void testNQueenApp() {
        SolverFactory<NQueen> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(NQueen.class)
                .withEntityClasses(Queen.class)
                .withConstraintProviderClass(NQueenConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        NQueen problem = generateDemoData();
        Solver<NQueen> solver = solverFactory.buildSolver();
        NQueen solution = solver.solve(problem);
        printTimetable(solution);
    }


    public NQueen generateDemoData() {
        List<Column> columnList = new ArrayList<>();
        List<Row> rowList = new ArrayList<>();
        List<Queen> queenList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            columnList.add(new Column(i));
            rowList.add(new Row(i));
            queenList.add(new Queen(i, null, null));
        }
        return new NQueen(columnList, rowList, queenList, null);
    }

    private void printTimetable(NQueen nQueen) {
        System.out.println("");
        List<Column> columnList = nQueen.getColumnList();
        List<Row> rowList = nQueen.getRowList();
        List<Queen> queenList = nQueen.getQueenList();
        Map<Column, Map<Row, List<Queen>>> queenMap = queenList.stream()
                .filter(queen -> queen.getColumn() != null && queen.getRow() != null)
                .collect(Collectors.groupingBy(Queen::getColumn, Collectors.groupingBy(Queen::getRow)));
        System.out.println("|     | " + columnList.stream()
                .map(room -> String.format("%-3s", room.getIndex())).collect(Collectors.joining(" | ")) + " |");
        System.out.println("|" + "-----|".repeat(columnList.size() + 1));
        for (Column column : columnList) {
            List<List<Queen>> cellList = rowList.stream()
                    .map(row -> {
                        Map<Row, List<Queen>> byRowMap = queenMap.get(column);
                        if (byRowMap == null) {
                            return Collections.<Queen>emptyList();
                        }
                        List<Queen> cellQueenList = byRowMap.get(row);
                        if (cellQueenList == null) {
                            return Collections.<Queen>emptyList();
                        }
                        return cellQueenList;
                    })
                    .collect(Collectors.toList());

            System.out.println("| " + String.format("%-3s", column.getIndex()) + " | "
                    + cellList.stream().map(cellQueenList -> String.format("%-3s",
                            cellQueenList.stream().map(queen -> queen.getId().toString()).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            System.out.println("|" + "-----|".repeat(columnList.size() + 1));
        }
        List<Queen> unassignedQueens = queenList.stream()
                .filter(Queen -> Queen.getColumn() == null || Queen.getRow() == null)
                .collect(Collectors.toList());
        if (!unassignedQueens.isEmpty()) {
            System.out.println("");
            System.out.println("Unassigned Queens");
            for (Queen Queen : unassignedQueens) {
                System.out.println("  " + Queen.getColumn() + " - " + Queen.getRow());
            }
        }
    }
}