package model;

/**
 * An enum type the represent the type of move a pawn made.
 */
public enum PawnMove
{
    /**
     * One step forward.
     */
    SIMPLE,
    /**
     * Two steps forward.
     */
    DOUBLE,
    /**
     * One step forward diagonally.
     */
    DIAGONAL,
    /**
     * Invalid move.
     */
    INVALID
}
