package model;

/**
 * A factory class to create the chess pieces
 */
public class ChessPieceFactory
{
    private PieceType pieceType;
    private PieceColor pieceColor;
    private int locationX = 0;
    private int locationY = 0;

    /**
     * initializes ChessPieceFactory
     * @param pieceType the type of the chess piece
     * @param pieceColor the color of the chess piece
     */
    public ChessPieceFactory(PieceType pieceType, PieceColor pieceColor)
    {
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
    }

    /**
     * Sets the position of the piece
     * @param position the position of the piece 
     * @return this factory to concatenate other method calls
     */

    public ChessPieceFactory withPosition(Position position)
    {
        this.locationX = position.getX();
        this.locationY = position.getY();
        return this;
    }

    /**
     * Creates a new chess piece
     * @return the created piece
     */
    public ChessPieceImpl build()
    {
        return new ChessPieceImpl("Chess" + "_" + pieceType.toString()
            .toLowerCase() + "_"
                + (this.pieceColor == PieceColor.BLACK ? "d" : "l") + ".png",
                this.pieceType = pieceType, this.pieceColor = pieceColor,
                new Position(this.locationX, this.locationY));
    }

}
