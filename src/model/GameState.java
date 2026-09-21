package model;

/**
 * An enum type the represent the state of a chess game with.
 */
public enum GameState
{
    /**
     * The black king is in check.
     */
    BLACK_CHECK,
    /**
     * The white king is in check.
     */
    WHITE_CHECK,
    /**
     * The black king is in checkmate.
     */
    BLACK_CHECKMATE,
    /**
     * The white king is in checkmate.
     */
    WHITE_CHECKMATE,
    /**
     * The game is running normally, with no special state.
     */
    RUNNING,
    /**
     * The game state import has failed.
     */
    IMPORT_FAILED
}
