package model;

import java.util.List;
import java.util.Set;

/**
 * FenState represents the state of a chess board as defined by Forsyth–Edwards Notation (FEN).
 */
public class FenState
{
    private List<ChessPiece> activePieces;
    private PieceColor activeColor;
    private Set<CastlingRights> castlingAvailability;
    private Position enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    /**
     * Constructs a new FenState with the specified details.
     * 
     * @param activePieces The list of pieces currently on the board.
     * @param activeColor The color of the player whose turn it is.
     * @param castlingAvailability The set of available castling rights.
     * @param enPassantPosition The position for an en passant capture, or null if not applicable.
     * @param halfmoveClock The number of plies since the last pawn move or piece capture.
     * @param fullmoveNumber The current fullmove number.
     * 
     * @require activePieces != null
     * @require activeColor != null
     * @require castlingAvailability != null
     */
    public FenState(List<ChessPiece> activePieces, PieceColor activeColor,
            Set<CastlingRights> castlingAvailability,
            Position enPassantPosition, int halfmoveClock, int fullmoveNumber)
    {
        assert activePieces != null : "activePieces should not be null";
        assert activeColor != null : "activeColor should not be null";
        assert castlingAvailability != null : "castlingAvailability should not be null";

        this.activePieces = activePieces;
        this.activeColor = activeColor;
        this.castlingAvailability = castlingAvailability;
        this.enPassantSquare = enPassantPosition;
        this.halfmoveClock = halfmoveClock;
        this.fullmoveNumber = fullmoveNumber;
    }

    /**
     * Returns the list of pieces currently on the board.
     * 
     * @return The list of active pieces.
     */
    public List<ChessPiece> getActivePieces()
    {
        return activePieces;
    }

    /**
     * Returns the color of the player whose turn it is.
     * 
     * @return The active piece color.
     */
    public PieceColor getActiveColor()
    {
        return activeColor;
    }

    /**
     * Returns the position for an en passant capture, or null if not applicable.
     * 
     * @return The en passant position.
     */
    public Position getEnPassantSquare()
    {
        return enPassantSquare;
    }

    /**
     * Returns the set of available castling rights.
     * 
     * @return The set of castling rights.
     */
    public Set<CastlingRights> getCastlingAvailability()
    {
        return castlingAvailability;
    }

    /**
     * Returns the halfmove clock value.
     * 
     * @return The halfmove clock.
     */
    public int getHalfmoveClock()
    {
        return halfmoveClock;
    }

    /**
     * Returns the fullmove number.
     * 
     * @return The fullmove number.
     */
    public int getFullmoveNumber()
    {
        return fullmoveNumber;
    }
}
