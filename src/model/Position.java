package model;

/**
 * This record represents the position of a chess piece
 * 
 * @param x The position along the x-axis (file)
 * @param y The position along the y-axis (rank)
 * 
 * @require x between 1 and 8
 * @require y between 1 and 8
 */
public record Position(int x, int y)
{

    /**
     * Creates an instance of a Position record class.
     * 
     * @param x Position along the x-axis (file)
     * @param y Position along the y-axis (rank)
     * 
     * @require x between 1 and 8
     * @require y between 1 and 8
     */
    public Position(int x, int y)
    {
        assert x >= 1 && x <= 8 : "Respect value range";
        assert y >= 1 && y <= 8 : "Respect value range";

        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the position.
     * 
     * @return The x-coordinate.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the y-coordinate of the position.
     * 
     * @return The y-coordinate.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Checks if the position is equal to another position.
     * 
     * @param p The position to compare with.
     * @return true if the positions are equal, false otherwise.
     */
    public boolean isEqualTo(Position p)
    {
        if (p == null) return false;
        return (p.getX() == this.x && p.getY() == this.y);
    }

    /**
     * Returns the string representation of the position.
     * 
     * @return A string in the format "x,y".
     */
    public String toString()
    {
        return "" + x + "," + y;
    }

}
