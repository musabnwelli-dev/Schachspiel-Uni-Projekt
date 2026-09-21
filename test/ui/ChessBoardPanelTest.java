package ui;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.ChessGame;
import model.Position;

class ChessBoardPanelTest
{

    ChessBoardPanel board;

    @BeforeEach
    public void setup()
    {
        board = new ChessBoardPanel(new ChessGame(), new PlayerIndicator(),
                new PlayerIndicator());
    }

    @Test
    void testGetPreferredSize()
    {

        Dimension expectedDimension = new Dimension(720, 720);

        assertTrue(expectedDimension.equals(board.getPreferredSize()));

    }

    @Test
    void testGetSquareSize()
    {

        assertEquals(90, board.getSquareSize());

    }

    @Test
    void testGetSelectedPieceAfterSetSelectedPiece()
    {
        board.setSelectedPieceIndex(2);

        assertEquals(2, board.getSelectedPieceIndex());

    }

    @Test
    void testGetSelectionXAndGetSelectionYAfterSetSelection()
    {
        board.setSelection(2, 3);

        assertEquals(2, board.getSelectionX());
        assertEquals(3, board.getSelectionY());

    }

    @Test
    void testClearSelection()
    {
        board.clearSelection();

        assertEquals(-1, board.getSelectionX());
        assertEquals(-1, board.getSelectionY());

    }

    @Test
    void testGetLegalPositionAfterSetLegalPosition()
    {
        List<Position> legalPositions = new ArrayList<Position>();
        legalPositions.add(new Position(1, 2));
        legalPositions.add(new Position(3, 4));
        board.setLegalPositions(legalPositions);
        assertEquals(legalPositions, board.getLegalPositions());
    }

    @Test
    void testClearLegalPosition()
    {
        List<Position> legalPositions = new ArrayList<Position>();
        legalPositions.add(new Position(1, 2));
        legalPositions.add(new Position(3, 4));
        board.setLegalPositions(legalPositions);
        board.clearLegalPositions();
        assertTrue(legalPositions.isEmpty());

    }

    @Test
    void testClearLegalPositionException()
    {
        List<Position> legalPositions = new ArrayList<Position>();
        assertThrows(NullPointerException.class, () -> {
            board.setLegalPositions(null);
        });

    }

    @Test
    void testClearPlayer()
    {
        PlayerIndicator whitePlayer = new PlayerIndicator();
        PlayerIndicator blackPlayer = new PlayerIndicator();
        whitePlayer.setColor(Color.BLUE);
        blackPlayer.setColor(Color.RED);
        ChessBoardPanel board = new ChessBoardPanel(new ChessGame(),
                whitePlayer, blackPlayer);
        board.clearPlayer();
        // Standard: white player begins and black player has light-gray color
        assertEquals(Color.WHITE, whitePlayer.getColor());
        assertEquals(Color.LIGHT_GRAY, blackPlayer.getColor());

    }

}
