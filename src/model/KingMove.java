package model;

/**
 * An enum type the represent the type of move a king made.
 */
public enum KingMove
{
    /**
     * One step in any direction.
     */
    SIMPLE,
    /**
     * Two steps on the x-axis.
     */
    CASTLING,
    /**
     * Invalid move.
     */
    INVALID
}
