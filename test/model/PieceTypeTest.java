package model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class PieceTypeTest
{

    @Test
    void testGetCanMoveToPositiveForPawn()
    {
        ChessPieceImpl pawnPieceImpl = new ChessPieceImpl("pawn",
                PieceType.PAWN, PieceColor.WHITE, new Position(1, 2));

        // Pawn moves one field
        assertTrue(pawnPieceImpl.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 1));
        // Pawn can also move two fields at the beginning
        assertTrue(pawnPieceImpl.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 2));

        // Pawn can also move diagonally
        assertTrue(pawnPieceImpl.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 1));

        //test also black

        // Pawn moves one field
        ChessPieceImpl pawnPieceBlack = new ChessPieceImpl("pawn",
                PieceType.PAWN, PieceColor.BLACK, new Position(1, 7));

        assertTrue(pawnPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -1));

        // Pawn can also move two fields at the beginning
        assertTrue(pawnPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -2));
        // Pawn can also move diagonally
        assertTrue(pawnPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, -1, -1));

    }

    @Test
    void testGetCanMoveToNegativeForPawn()
    {
        ChessPieceImpl pawnPieceImpl = new ChessPieceImpl("pawn",
                PieceType.PAWN, PieceColor.WHITE, new Position(1, 2));
        //Fall 1
        assertFalse(pawnPieceImpl.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 3));
        //Fall 2
        assertFalse(pawnPieceImpl.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 2));

        //test also black
        ChessPieceImpl pawnPieceBlack = new ChessPieceImpl("pawn",
                PieceType.PAWN, PieceColor.BLACK, new Position(1, 7));

        assertFalse(pawnPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -3));

    }

    @Test
    void testGetCanMoveToPositiveForRook()
    {
        ChessPieceImpl rookPieceWhite = new ChessPieceImpl("rook",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));

        // rook moves one field vertically
        assertTrue(rookPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 1));
        // rook can also move more than one field vertically
        assertTrue(rookPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 5));
        // rook can also move horizontally
        assertTrue(rookPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 3, 0));

        // test black
        // rook moves one field vertically
        ChessPieceImpl rookPieceBlack = new ChessPieceImpl("rook",
                PieceType.ROOK, PieceColor.BLACK, new Position(1, 8));
        assertTrue(rookPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -1));
        // rook can also move more than one field vertically
        assertTrue(rookPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -5));
        // rook can also move horizontally
        assertTrue(rookPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 3, 0));

    }

    @Test
    void testGetCanMoveToNegativeForRook()
    {
        ChessPieceImpl rookPieceWhite = new ChessPieceImpl("rook",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));

        assertFalse(rookPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 2, 2));

        // test black
        ChessPieceImpl rookPieceBlack = new ChessPieceImpl("rook",
                PieceType.ROOK, PieceColor.BLACK, new Position(1, 8));
        assertFalse(rookPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, -2, -2));

    }

    @Test
    void testGetCanMoveToPositiveForKnight()
    {
        ChessPieceImpl knightPieceWhite = new ChessPieceImpl("knight",
                PieceType.KNIGHT, PieceColor.WHITE, new Position(2, 1));

        assertTrue(knightPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 2, 1));
        //another side
        assertTrue(knightPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 2));

        // test black
        ChessPieceImpl knightPieceBlack = new ChessPieceImpl("knight",
                PieceType.KNIGHT, PieceColor.BLACK, new Position(2, 8));
        assertTrue(knightPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 2, -1));
        //another side
        assertTrue(knightPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 1, -2));

    }

    @Test
    void testGetCanMoveToNegativeForKnight()
    {
        ChessPieceImpl knightPieceWhite = new ChessPieceImpl("knight",
                PieceType.KNIGHT, PieceColor.WHITE, new Position(2, 1));

        assertFalse(knightPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 2, 2));

        // test black
        ChessPieceImpl knightPieceBlack = new ChessPieceImpl("knight",
                PieceType.KNIGHT, PieceColor.BLACK, new Position(2, 8));

        assertFalse(knightPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 2, -2));

    }

    @Test
    void testGetCanMoveToPositiveForBishop()
    {
        ChessPieceImpl bishopPieceWhite = new ChessPieceImpl("bishop",
                PieceType.BISHOP, PieceColor.WHITE, new Position(3, 1));

        assertTrue(bishopPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 2, 2));
        //diagonally on the other side
        assertTrue(bishopPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, -2, -2));

        // test black
        ChessPieceImpl bishopPieceBlack = new ChessPieceImpl("bishop",
                PieceType.BISHOP, PieceColor.BLACK, new Position(3, 8));

        assertTrue(bishopPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 2, 2));

        //diagonally on the other side
        assertTrue(bishopPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, -2, -2));

    }

    @Test
    void testGetCanMoveToNegativeForBishop()
    {
        ChessPieceImpl bishopPieceWhite = new ChessPieceImpl("bishop",
                PieceType.BISHOP, PieceColor.WHITE, new Position(3, 1));

        assertFalse(bishopPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 2));

        //test black
        ChessPieceImpl bishopPieceBlack = new ChessPieceImpl("bishop",
                PieceType.BISHOP, PieceColor.BLACK, new Position(3, 8));

        assertFalse(bishopPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, 2));

    }

    @Test
    void testGetCanMoveToPositiveForQueen()
    {
        ChessPieceImpl queenPieceWhite = new ChessPieceImpl("queen",
                PieceType.QUEEN, PieceColor.WHITE, new Position(4, 1));

        // vertical
        assertTrue(queenPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 1));

        // horizontal
        assertTrue(queenPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 0));

        // diagonal
        assertTrue(queenPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 1));

        // test black
        ChessPieceImpl queenPieceBlack = new ChessPieceImpl("queen",
                PieceType.QUEEN, PieceColor.BLACK, new Position(4, 8));

        // vertical
        assertTrue(queenPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, 1));

        // horizontal
        assertTrue(queenPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 1, 0));

        // diagonal
        assertTrue(queenPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 1, 1));

    }

    @Test
    void testGetCanMoveToNegativeForQueen()
    {
        ChessPieceImpl queenPieceWhite = new ChessPieceImpl("queen",
                PieceType.QUEEN, PieceColor.WHITE, new Position(4, 1));

        assertFalse(queenPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 0));

        // test black

        ChessPieceImpl queenPieceBlack = new ChessPieceImpl("queen",
                PieceType.QUEEN, PieceColor.BLACK, new Position(4, 8));

        assertFalse(queenPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, 0));

    }

    @Test
    void testGetCanMoveToPositiveForKing()
    {
        ChessPieceImpl kingPieceWhite = new ChessPieceImpl("king",
                PieceType.KING, PieceColor.WHITE, new Position(5, 1));

        // vertical
        assertTrue(kingPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 1));

        // horizontal
        assertTrue(kingPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 0));

        // diagonal
        assertTrue(kingPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 1, 1));

        // test black

        ChessPieceImpl kingPieceBlack = new ChessPieceImpl("king",
                PieceType.KING, PieceColor.BLACK, new Position(5, 8));

        // vertical
        assertTrue(kingPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, -1));

        // horizontal
        assertTrue(kingPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, -1, 0));

        // diagonal
        assertTrue(kingPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, -1, -1));
    }

    @Test
    void testGetCanMoveToNegativeForKing()
    {
        ChessPieceImpl kingPieceWhite = new ChessPieceImpl("king",
                PieceType.KING, PieceColor.WHITE, new Position(5, 1));

        assertFalse(kingPieceWhite.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.WHITE, 0, 3));

        // test black
        ChessPieceImpl kingPieceBlack = new ChessPieceImpl("king",
                PieceType.KING, PieceColor.BLACK, new Position(5, 8));

        assertFalse(kingPieceBlack.getPieceType()
            .getCanMoveTo()
            .test(PieceColor.BLACK, 0, 3));

    }

}
