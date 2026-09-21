package model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ChessPieceTest
{

    @Test
    void testGetPositionAfterSetPosition()
    {
        //given

        ChessPieceImpl piece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.WHITE, new Position(1, 2));
        //when
        piece.setPosition(new Position(1, 3));
        Position expectedPosition = new Position(1, 3);

        //then
        assertTrue(expectedPosition.isEqualTo(piece.getPosition()));
    }

    @Test
    void testGetPieceColorPositive()
    {
        //Fall: Farbe Weiß
        ChessPieceImpl whitePiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.WHITE, new Position(1, 2));

        assertEquals(PieceColor.WHITE, whitePiece.getPieceColor());

        //Fall: Farbe schwarz
        ChessPieceImpl blackPiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.BLACK, new Position(1, 7));

        assertEquals(PieceColor.BLACK, blackPiece.getPieceColor());

    }

    @Test
    void testGetPieceColorNegative()
    {
        //Fall: Farbe Weiß
        ChessPieceImpl whitePiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.WHITE, new Position(1, 2));

        //then
        assertNotEquals(PieceColor.BLACK, whitePiece.getPieceColor());

        //Fall: Farbe schwarz

        ChessPieceImpl blackPiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.BLACK, new Position(1, 7));

        assertNotEquals(PieceColor.WHITE, blackPiece.getPieceColor());

    }

    @Test
    void testGetImagePositive()
    {

        ChessPieceImpl rookPieceImpl = new ChessPieceImpl("Chess_rook_l.png",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));

        assertEquals("Chess_rook_l.png", rookPieceImpl.getImage());

    }

    @Test
    void testGetImageNegative()
    {

        ChessPieceImpl rookPiece = new ChessPieceImpl("Chess_rook_l.png",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));

        assertNotEquals("Chess_king_l.png", rookPiece.getImage());

    }

    @Test
    void testValidateMovePositive()
    {
        //Fall 1: Pawn

        ChessPieceImpl pawnPiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.WHITE, new Position(1, 2));

        assertTrue(pawnPiece.validateMove(
                new Move(pawnPiece.getPosition(), new Position(1, 3))));

        //Fall 2: Rook

        ChessPieceImpl rookPiece = new ChessPieceImpl("Chess_rook_l.png",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));
        // remove Pawn from the way so that the knight can make a move 
        pawnPiece.setPosition(new Position(1, 4));
        assertTrue(rookPiece.validateMove(
                new Move(rookPiece.getPosition(), new Position(1, 3))));

        // Fall 3: knight

        ChessPieceImpl knightPiece = new ChessPieceImpl("knight",
                PieceType.KNIGHT, PieceColor.WHITE, new Position(2, 1));
        assertTrue(knightPiece.validateMove(
                new Move(knightPiece.getPosition(), new Position(3, 3))));

    }

    @Test
    void testValidateMoveNegative()
    {
        //Fall 1: Pawn

        ChessPieceImpl pawnPiece = new ChessPieceImpl("pawn", PieceType.PAWN,
                PieceColor.WHITE, new Position(1, 2));

        //then
        assertFalse(pawnPiece.validateMove(
                new Move(pawnPiece.getPosition(), new Position(1, 6))));

        //Fall 2: Rook

        ChessPieceImpl rookPiece = new ChessPieceImpl("Chess_rook_l.png",
                PieceType.ROOK, PieceColor.WHITE, new Position(1, 1));

        assertFalse(rookPiece.validateMove(
                new Move(rookPiece.getPosition(), new Position(2, 2))));

    }

}
