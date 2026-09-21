package model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChessPieceFactoryTest
{

    @Test
    void testWithPosition()
    {
        ChessPiece rookPiece = new ChessPieceFactory(PieceType.ROOK,
                PieceColor.WHITE).withPosition(new Position(1, 1))
                    .build();
        Position expectedPosition = new Position(1, 1);
        assertTrue(expectedPosition.isEqualTo(rookPiece.getPosition()));

    }

    @Test
    void testBuildPositive()
    {
        ChessPiece rookPiece = new ChessPieceFactory(PieceType.ROOK,
                PieceColor.WHITE).withPosition(new Position(1, 1))
                    .build();
        // check the type of the chess piece
        assertEquals(PieceType.ROOK, rookPiece.getPieceType());
        // check the color of the chess piece
        assertEquals(PieceColor.WHITE, rookPiece.getPieceColor());

    }

}
