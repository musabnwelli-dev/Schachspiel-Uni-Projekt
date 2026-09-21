package model;

/**
 * A concrete class which implements ChessPiece
 */

public class ChessPieceImpl implements ChessPiece
{
    private final String image;
    private final PieceType pieceType;
    private final PieceColor pieceColor;
    private Position position;

    /**
     * Initializes ChessPieceImpl
     * @param image  the image of the chess piece
     * @param pieceType the type of the chess piece
     * @param pieceColor the color of the chess piece
     * @param position  the position of the chess piece
     */
    public ChessPieceImpl(String image, PieceType pieceType,
            PieceColor pieceColor, Position position)
    {
        this.image = image;
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        this.position = position;
    }

    @Override
    public String getImage()
    {
        return image;
    }

    @Override
    public PieceType getPieceType()
    {
        return pieceType;
    }

    @Override
    public PieceColor getPieceColor()
    {
        return pieceColor;
    }

    @Override
    public Position getPosition()
    {

        return position;
    }

    @Override
    public void setPosition(Position position)
    {

        this.position = position;
    }

    @Override
    public boolean validateMove(Move move)
    {
        int currX = move.getCurrentPosition()
            .getX();
        int currY = move.getCurrentPosition()
            .getY();

        int targX = move.getNewPosition()
            .getX();
        int targY = move.getNewPosition()
            .getY();

        int movementX = targX - currX;
        int movementY = targY - currY;

        return pieceType.getCanMoveTo()
            .test(pieceColor, movementX, movementY);
    }

}
