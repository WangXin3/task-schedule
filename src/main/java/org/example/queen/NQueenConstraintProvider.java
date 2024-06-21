package org.example.queen;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

/**
 * @author wangxin
 * @since 2024/6/18 9:41
 */


public class NQueenConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // 列冲突
                columnConflict(constraintFactory),
                // 行冲突
                rowConflict(constraintFactory),
                // 升序对角线冲突
                ascendingDiagonalIndexConflict(constraintFactory),
                // 降序对角线冲突
                descendingDiagonalIndexConflict(constraintFactory),
        };
    }

    public Constraint columnConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Queen.class)
                .join(Queen.class,
                        Joiners.equal(Queen::getColumn),
                        Joiners.lessThan(Queen::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Column conflict");
    }

    public Constraint rowConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Queen.class)
                .join(Queen.class,
                        Joiners.equal(Queen::getRow),
                        Joiners.lessThan(Queen::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Row conflict");
    }

    public Constraint ascendingDiagonalIndexConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Queen.class)
                .join(Queen.class,
                        Joiners.equal(Queen::getAscendingDiagonalIndex),
                        Joiners.lessThan(Queen::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("AscendingDiagonalIndex conflict");
    }

    public Constraint descendingDiagonalIndexConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Queen.class)
                .join(Queen.class,
                        Joiners.equal(Queen::getDescendingDiagonalIndex),
                        Joiners.lessThan(Queen::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("DescendingDiagonalIndex conflict");
    }

}

