package model;

import unit.TriPredicate;

/**
 * The types of chess pieces
 */
public enum PieceType
{
    KING((pieceColor, movementX, movementY) -> {
        // 1 field horizontal
        // 1 field vertikal
        // 1 field diagonal
    	// 2 fields horizontal (castling)

        return ((Math.abs(movementX) <= 1) && (Math.abs(movementY) <= 1)
                && (Math.abs(movementX) + Math.abs(movementY) != 0))
                || (Math.abs(movementX) == 2 && Math.abs(movementY) == 0);
    }), QUEEN((color, movementX, movementY) -> {
        //diagonal
        //horizontal
        //vertikal
        return (Math.abs(movementX) == Math.abs(movementY) && movementX != 0)
                || (movementX != 0 && movementY == 0)
                || (movementX == 0 && movementY != 0);
    }),

    ROOK((pieceColor, movementX, movementY) -> {

        return (movementX != 0 && movementY == 0)
                || (movementX == 0 && movementY != 0);

    }), KNIGHT((pieceColor, movementX, movementY) -> {
        // 2 fields x-direction and 1 field Y-direction
        // 2 fields Y-direction  and 1 fied x-direction
        return (Math.abs(movementX) == 2 && Math.abs(movementY) == 1)
                || (Math.abs(movementX) == 1 && Math.abs(movementY) == 2);
    }),

    BISHOP((pieceColor, movementX, movementY) -> {
        // only diagonal
        return (Math.abs(movementX) == Math.abs(movementY) && movementX != 0);

    }), PAWN((pieceColor, movementX, movementY) -> {
        // 1 field above or under
        // at the beginning 2 fields
        // 1 field Diagonal
        return (movementY == (pieceColor == PieceColor.WHITE ? 1 : -1)
                && movementX == 0)
                || (movementY == (pieceColor == PieceColor.WHITE ? 2 : -2)
                        && movementX == 0)
                || (movementY == (pieceColor == PieceColor.WHITE ? 1 : -1)
                        && Math.abs(movementX) == 1);
    });

    // PieceColor, MovementX, MovementY

    private TriPredicate<PieceColor, Integer, Integer> canMoveTo;

    private PieceType(TriPredicate<PieceColor, Integer, Integer> canMoveTo)
    {
        this.canMoveTo = canMoveTo;
    }

    /**
     * Returns the moving rule of the piece
     * @return moving rule as Tripredicate
     */

    public TriPredicate<PieceColor, Integer, Integer> getCanMoveTo()
    {
        return canMoveTo;
    }

}
