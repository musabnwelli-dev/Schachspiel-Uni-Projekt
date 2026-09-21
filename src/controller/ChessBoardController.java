package controller;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;

import model.ChessGame;
import model.ChessPiece;
import model.GameState;
import model.Move;
import model.PieceColor;
import model.Position;
import ui.ChessBoardPanel;
import ui.PlayerIndicator;

/**
 * this class that reacts to Mouse events
 */
public class ChessBoardController extends MouseAdapter
{

    private ChessBoardPanel chessBoardPanel;
    private ChessGame chessGame;
    private boolean isMoveLegal;
    private PlayerIndicator whiteIndicator;
    private PlayerIndicator blackIndicator;

    /**
     * initializes ChessBoardController
     * @param chessBoardPanel the board of the chess game
     * @param chessGame the game logic
     * @param whiteIndicator indicator for the white player
     * @param blackIndicator indicator for the black player
     */
    public ChessBoardController(ChessBoardPanel chessBoardPanel,
            ChessGame chessGame, PlayerIndicator whiteIndicator,
            PlayerIndicator blackIndicator)
    {

        this.chessBoardPanel = chessBoardPanel;
        this.chessGame = chessGame;
        this.whiteIndicator = whiteIndicator;
        this.blackIndicator = blackIndicator;

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int selectionX = (e.getX() / chessBoardPanel.getSquareSize()) + 1;
        int selectionY = (e.getY() / chessBoardPanel.getSquareSize()) + 1;
        chessBoardPanel.setSelection(selectionX, selectionY);

        if (chessBoardPanel.getSelectedPieceIndex() == -1)
        {

            for (int i = 0; i < chessGame.getActivePieces()
                .size(); i++)
            {
                ChessPiece piece = chessGame.getActivePieces()
                    .get(i);
                if (piece.getPosition()
                    .getX() == selectionX
                        && piece.getPosition()
                            .getY() == selectionY)
                {
                    chessBoardPanel.setSelectedPieceIndex(i);
                    chessBoardPanel.setSelectedPiece(piece);
                    List<Position> legalPositions = chessGame
                        .getLegalPositionsForPiece(piece);
                    chessBoardPanel.setLegalPositions(legalPositions);
                }
            }

        }
        else
        {
            ChessPiece currentPiece = chessBoardPanel.getSelectedPiece();
            Position currentPosition = currentPiece.getPosition();
            Position newPosition = new Position(selectionX, selectionY);
            Move move = new Move(currentPosition, newPosition);

            boolean isMoveLegal = chessGame.applyMove(move);
            chessBoardPanel.clearLegalPositions();
            this.isMoveLegal = isMoveLegal;
            if (chessGame.getActivePlayer() == PieceColor.WHITE && isMoveLegal)
            {
                blackIndicator.setColor(Color.LIGHT_GRAY);
                whiteIndicator.setColor(Color.WHITE);

            }
            else if (chessGame.getActivePlayer() == PieceColor.BLACK
                    && isMoveLegal)
            {
                whiteIndicator.setColor(Color.LIGHT_GRAY);
                blackIndicator.setColor(Color.BLACK);

            }

            chessBoardPanel.clearSelection();

        }

        chessBoardPanel.repaint();
        if (this.isMoveLegal && chessBoardPanel.getSelectedPieceIndex() == -1)
        {
            checkGameState();
        }

    }

    /**
     * Checks the current game state and displays the corresponding dialog
     */
    public void checkGameState()
    {
        GameState state = chessGame.getGameState();
        if (state == GameState.BLACK_CHECK)
        {
            JOptionPane.showMessageDialog(chessBoardPanel,
                    "Der schwarze König steht im Schach!");
        }
        if (state == GameState.WHITE_CHECK)
        {
            JOptionPane.showMessageDialog(chessBoardPanel,
                    "Der weiße König steht im Schach!");
        }
        if (state == GameState.BLACK_CHECKMATE)
        {

            int result = JOptionPane.showConfirmDialog(chessBoardPanel,
                    "Schachmatt! Weiß hat gewonnen. \nmöchtest du ein neues Spiel starten? ",
                    "Spiel beendet", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION)
            {
                chessGame.resetBoard();
                chessBoardPanel.repaint();
                blackIndicator.setColor(Color.LIGHT_GRAY);
                whiteIndicator.setColor(Color.WHITE);
            }
            else if (result == JOptionPane.CANCEL_OPTION)
            {
                // do nothing
            }

        }
        if (state == GameState.WHITE_CHECKMATE)
        {

            int result = JOptionPane.showConfirmDialog(chessBoardPanel,
                    "Schachmatt! Schwarz hat gewonnen. \nmöchtest du ein neues Spiel starten? ",
                    "Spiel beendet", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION)
            {
                chessGame.resetBoard();
                chessBoardPanel.repaint();
                blackIndicator.setColor(Color.LIGHT_GRAY);
                whiteIndicator.setColor(Color.WHITE);
            }
            else if (result == JOptionPane.CANCEL_OPTION)
            {
                // do nothing
            }
        }

    }

}
