package model;

/**
 * Interface between ChessGame (the application) and  ChesspieceImpl (the implementation)
 * Seperates the application from the implementation
 */
public interface ChessPiece
{
    /**
     * Returns the position of a chess piece
     * @return the Position of a chess piece
     */
    Position getPosition();

    /**
     * Sets the position of a chess piece
     * @param position the new position of a chess piece
     */

    void setPosition(Position position);

    /**
     * Returns the type of the chess piece
     * @return chess piece
     */

    PieceType getPieceType();

    /**
     * Returns the color of a chess piece
     * @return the color of the chess piece
     */

    PieceColor getPieceColor();

    /**
     * Returns the path of the image 
     * @return the path of the image
     */

    String getImage();

    /**
     * Checks, if a piece moves correctly using its own moving rules
     * @param move the move to validate
     * @return true, if the moving rule is valid, otherweise false
     */

    boolean validateMove(Move move);

}
