package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import controller.ChessBoardController;
import model.ChessGame;
import model.ChessPiece;
import model.PieceColor;
import model.Position;

/**
 * The board of the chess game
 */
public class ChessBoardPanel extends JPanel
{
    final int boardSize = 8;
    final int squareSize = 90;
    private ChessGame chessGame;
    private int selectionX = -1; //No selection made
    private int selectionY = -1; //No selection made
    private int selectedPieceIndex = -1; //No piece selected
    private ChessPiece selectedPiece = null;
    private PlayerIndicator whiteIndicator;
    private PlayerIndicator blackIndicator;

    private List<Position> legalPositions = new ArrayList<Position>();

    /**
     * initializes ChessBoardPanel
     * @param chessGame the game logic
     * @param whiteIndicator indicator for the white player
     * @param blackIndicator indicator for the black player
     */
    public ChessBoardPanel(ChessGame chessGame, PlayerIndicator whiteIndicator,
            PlayerIndicator blackIndicator)
    {
        this.chessGame = chessGame;
        this.whiteIndicator = whiteIndicator;
        this.blackIndicator = blackIndicator;

        addMouseListener(new ChessBoardController(this, this.chessGame,
                this.whiteIndicator, this.blackIndicator));

    }

    @Override
    protected void paintComponent(Graphics g)
    {
        for (int row = 1; row <= boardSize; row++)
        {
            for (int col = 1; col <= boardSize; col++)
            {
                if (selectionX == col && selectionY == row)
                {
                    g.setColor(Color.BLUE);

                }

                else if ((col + row) % 2 == 0)
                {
                    g.setColor(Color.DARK_GRAY);
                }
                else
                {
                    g.setColor(Color.WHITE);

                }
                g.fillRect((col - 1) * squareSize, (row - 1) * squareSize,
                        squareSize, squareSize);

            }
        }

        for (ChessPiece chessPiece : chessGame.getActivePieces())
        {

            final InputStream inputStream = ChessBoardPanel.class
                .getResourceAsStream("/" + chessPiece.getImage());
            try
            {
                final Image image = ImageIO.read(inputStream);
                g.drawImage(image, (chessPiece.getPosition()
                    .getX() - 1) * squareSize,
                        (chessPiece.getPosition()
                            .getY() - 1) * squareSize,
                        squareSize, squareSize, this);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        for (Position selectedPosition : legalPositions)
        {
            g.setColor(Color.BLUE);
            int markerSize = squareSize / 3;
            int markerOffset = (squareSize - markerSize) / 2;

            g.fillRect(
                    (selectedPosition.getX() - 1) * squareSize + markerOffset,
                    (selectedPosition.getY() - 1) * squareSize + markerOffset,
                    markerSize, markerSize);

        }

    }

    @Override
    public Dimension getPreferredSize()
    {

        return new Dimension(boardSize * squareSize, boardSize * squareSize);
    }

    /**
     * Returns the size of the field
     * @return the size of the field
     */
    public int getSquareSize()
    {
        return squareSize;
    }

    /**
     * Returns the index of the selected piece
     * @return the index of the selected piece 
     */
    public int getSelectedPieceIndex()
    {
        return selectedPieceIndex;
    }

    /**
     * Returns the selected piece
     * @return the selected piece
     */
    public ChessPiece getSelectedPiece()
    {
        return selectedPiece;
    }

    /**
     * Sets the index of the selected piece
     * @param selectedPieceIndex the index to be set
     */
    public void setSelectedPieceIndex(int selectedPieceIndex)
    {
        this.selectedPieceIndex = selectedPieceIndex;
    }

    /**
     * Sets the selected piece
     * @param piece the piece to select
     */
    public void setSelectedPiece(ChessPiece piece)
    {
        selectedPiece = piece;
    }

    /**
     * Sets the selection of the piece
     * @param selectionX the selection in the x-coordinate
     * @param selectionY the selection in y-coordinate
     */
    public void setSelection(int selectionX, int selectionY)
    {
        this.selectionX = selectionX;
        this.selectionY = selectionY;
    }

    /**
     * Removes the selection
     */
    public void clearSelection()
    {
        selectionX = selectionY = selectedPieceIndex = -1;
        selectedPiece = null;
    }

    /**
     * Returns a list with the legal positions 
     * @return list with the legal positions
     */
    public List<Position> getLegalPositions()
    {

        return legalPositions;
    }

    /**
     * Sets the list of the legal positions
     * @param legalPositions the list with the legal positions
     */
    public void setLegalPositions(List<Position> legalPositions)
    {
        if (legalPositions == null)
        {
            throw new NullPointerException("Die Liste darf nicht null sein!");
        }
        this.legalPositions = legalPositions;
        repaint();
    }

    /**
     * Removes the positions from the list
     */
    public void clearLegalPositions()
    {
        this.legalPositions.clear();
    }

    /**
     * Resets the player-display 
     */
    public void clearPlayer()
    {
        if (chessGame.getActivePlayer() == PieceColor.WHITE)
        {
            blackIndicator.setColor(Color.LIGHT_GRAY);
            whiteIndicator.setColor(Color.WHITE);

        }
        else if (chessGame.getActivePlayer() == PieceColor.BLACK)
        {
            whiteIndicator.setColor(Color.LIGHT_GRAY);
            blackIndicator.setColor(Color.BLACK);
        }
    }

    /**
     * Returns the selection in the x-coordinate
     * @return selectionX
     */
    public int getSelectionX()
    {
        return selectionX;
    }

    /**
     * Returns the selection in the y-coordinate
     * @return selectionY
     */
    public int getSelectionY()
    {
        return selectionY;
    }

}
