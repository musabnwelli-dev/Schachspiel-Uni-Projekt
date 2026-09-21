package model;

import java.util.Comparator;

/**
 * Comparator for {@link ChessPiece}. Sorts pieces with higher rank and lower file first.
 */
public class ChessPieceComparator implements Comparator<ChessPiece>
{

    /**
     * Creates an instance of a ChessPieceComparator class.
     */
    public ChessPieceComparator()
    {
        super();
    }

    @Override
    public int compare(ChessPiece a, ChessPiece b)
    {
        assert a != null : "a should not be null";
        assert b != null : "b should not be null";
        Position aPos = a.getPosition();
        Position bPos = b.getPosition();
        if (a.getPieceType() == b.getPieceType()
                && a.getPieceColor() == b.getPieceColor()
                && aPos.isEqualTo(bPos))
        {
            return 0;
        }

        if (aPos.getY() > bPos.getY())
        {
            return -1;
        }

        if (aPos.getY() == bPos.getY() && aPos.getX() < bPos.getX())
        {
            return -1;
        }

        return 1;
    }
}
