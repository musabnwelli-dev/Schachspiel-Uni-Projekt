package model;

/**
 * Represents the move of a chess piece 
 */
public class Move
{
    private Position currentPosition;
    private Position newPosition;

    /**
     * Initializes Move 
     * @param currentPosition  the current position of the chess piece
     * @param newPosition      the new position of the chess piece
     */
    public Move(Position currentPosition, Position newPosition)
    {
        this.currentPosition = currentPosition;
        this.newPosition = newPosition;
    }

    /**
     * Returns the current position 
     * @return the current position
     */
    public Position getCurrentPosition()
    {
        return currentPosition;
    }

    /**
     * Returns the new position 
     * @return the new position
     */
    public Position getNewPosition()
    {
        return newPosition;
    }

}
