package model;

/**
 * An enum type to specify castling availability for each color/side in a chess game
 */
public enum CastlingRights
{
    /**
     * White king can castle queenside.
     */
    WHITE_QUEEN_SIDE,
    /**
     * White king can castle kingside.
     */
    WHITE_KING_SIDE,
    /**
     * Black king can castle queenside.
     */
    BLACK_QUEEN_SIDE,
    /**
     * Black king can castle kingside.
     */
    BLACK_KING_SIDE,
}
