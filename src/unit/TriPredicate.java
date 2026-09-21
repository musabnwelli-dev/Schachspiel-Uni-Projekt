package unit;

/**
 * This Interface is a helper interface for implementing the moving rules
 * @param <C> The Color of the chess piece (Piececolor)
 * @param <X> movementX
 * @param <Y> movementY
 */
public interface TriPredicate<C, X, Y>
{
    /**
     * tests the moving rules of the chess pieces 
     * @param c Piece color
     * @param x movementX
     * @param y movementY
     * @return true, if the rule is valid, otherweise false
     */
    public boolean test(C c, X x, Y y);

}
