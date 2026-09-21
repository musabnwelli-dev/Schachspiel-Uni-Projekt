package model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

/**
 * ChessBoard stores the state of a chess board and provides methods for retrieving and updating it.
 */
public class ChessBoard
{
    private List<ChessPiece> activePieces;
    private List<Move> moveHistory; // piece move history, not chess move history (castling adds two moves)
    private PieceColor activePlayer;
    private GameState state;
    private Position enPassantPosition;
    private Set<CastlingRights> castlingRights;
    private String importError;

    /**
     * Initializes the chess board with the default starting state.
     */
    public ChessBoard()
    {
        // default starting position in FEN
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Initializes the chess board state from a string in Forsyth–Edwards Notation (FEN).
     * @param fenString A FEN-compliant string
     * 
     * @require fenString != null
     */
    public ChessBoard(String fenString)
    {
        assert fenString != null : "fenString should not be null";

        // TODO: Reject FEN that would create an illegal position (e.g. opponent king can be captured immediately)?
        // TODO: Feat: use halfmove clock and fullmove number
        FenState fenState;
        try
        {
            fenState = FenStateConverter.fromString(fenString);
            this.activePieces = fenState.getActivePieces();
            this.activePlayer = fenState.getActiveColor();
            this.castlingRights = fenState.getCastlingAvailability();
            this.enPassantPosition = fenState.getEnPassantSquare();
            this.moveHistory = new ArrayList<Move>();
            this.state = GameState.RUNNING;
            this.importError = null;
        }
        catch (DataFormatException e)
        {
            // fall back to a minimal state to not return null in any other methods
            this.activePieces = new ArrayList<>();
            this.activePlayer = PieceColor.WHITE;
            this.castlingRights = EnumSet.noneOf(CastlingRights.class);
            this.enPassantPosition = null;
            this.moveHistory = new ArrayList<Move>();
            
            this.state = GameState.IMPORT_FAILED;
            this.importError = "DataFormatException while loading from FEN: "
                    + e.getMessage();
        }
    }

    /**
     * Exports the current chess board state as a FEN-compliant string.
     * 
     * Note: halfmove and fullmove are not yet implemented and use default values 0 and 1 respectively.
     * 
     * @return The FEN string
     */
    public String exportStateToFenString()
    {
        // TODO: replace halfmove and fullmove dummy
        FenState fenState = new FenState(
                new ArrayList<ChessPiece>(activePieces), activePlayer,
                EnumSet.copyOf(castlingRights), enPassantPosition, 0, 1);

        return FenStateConverter.toString(fenState);
    }

    /**
     * Returns details on the error that was caught during initialization.
     * 
     * @return A string containing details on the error, of null if there was no error
     */
    public String getImportError()
    {
        return importError;
    }

    /**
     * Returns an unmodifiable copy of the currently active pieces on the board.
     * 
     * @return Unmodifiable list of active pieces
     * @see ChessPiece
     */
    public List<ChessPiece> getActivePieces()
    {
        return List.copyOf(activePieces);
    }

    /**
     * Returns an unmodifiable copy of the piece movement history.
     * 
     * @return Unmodifiable list of moves
     * @see Move
     */
    public List<Move> getMoveHistory()
    {
        return List.copyOf(moveHistory);
    }

    /**
     * Returns the color of the currently active player.
     * 
     * @return Color of the active player
     * @see PieceColor
     */
    public PieceColor getActivePlayer()
    {
        return activePlayer;
    }

    /**
     * Returns the color of the current opponent player.
     * 
     * @return Color of the opponent
     * @see PieceColor
     */
    public PieceColor getOpponentColor()
    {
        return activePlayer == PieceColor.WHITE ? PieceColor.BLACK
                : PieceColor.WHITE;
    }

    /**
     * Returns the current state of the game.
     * 
     * @return State of the game
     * @see GameState
     */
    public GameState getState()
    {
        return state;
    }

    /**
     * Sets the state of the game to the given parameter.
     * 
     * @param newState The new state for the game
     * @see GameState
     * 
     * @require newState != null
     */
    public void setState(GameState newState)
    {
        assert newState != null : "The new state should not be null";
        state = newState;
    }

    /**
     * Returns the position a pawn can move to to capture an opponent pawn en passant.
     * 
     * @return The en passant position
     * @see Position
     */
    public Position getEnPassantPosition()
    {
        return enPassantPosition;
    }

    /**
     * Sets the position a pawn can move to to capture an opponent pawn en passant.
     * 
     * @param enPassantPosition The en passant position, or null if capturing en passant it not possible in the next turn
     * @see Position
     */
    public void setEnPassantPosition(Position enPassantPosition)
    {
        this.enPassantPosition = enPassantPosition;
    }

    /**
     * Returns the set of remaining castling rights for the current board state.
     * 
     * @return Set of remaining castling rights
     * @see CastlingRights
     */
    public Set<CastlingRights> getCastlingRights()
    {
        return castlingRights;
    }

    /**
     * Takes a set of castling rights that should be removed from the set of remaining castling rights.
     * 
     * @param rightsToRemove Set of castling rights to remove
     * @see CastlingRights
     * 
     * @require rightsToRemove != null
     */
    public void removeCastlingRights(Set<CastlingRights> rightsToRemove)
    {
        assert rightsToRemove != null;
        this.castlingRights.removeAll(rightsToRemove);
    }

    /**
     * Returns the piece at the given position.
     * 
     * @param position The position of the requested piece
     * @return The piece, of null if there was no piece at the given position
     * @see Position
     * @see ChessPiece
     * 
     * @require position != null
     */
    public ChessPiece getPieceAt(Position position)
    {
        assert position != null;

        for (ChessPiece piece : activePieces)
        {
            if (piece.getPosition()
                .isEqualTo(position))
            {
                return piece;
            }
        }
        return null;
    }

    /**
     * Returns the king piece of the active player.
     * 
     * @return King of the active player
     * @see ChessPiece
     */
    public ChessPiece getActiveKing()
    {
        ChessPiece king = null;

        for (ChessPiece piece : activePieces)
        {
            if (piece.getPieceType() == PieceType.KING
                    && piece.getPieceColor() == activePlayer)
            {
                king = piece;
                break;
            }
        }

        if (king == null)
        {
            throw new InternalError(
                    "No king for active player found, but it should always be there.");
        }

        return king;
    }

    /**
     * Captures a piece by removing it from the board.
     * 
     * @param piece The piece to capture
     * @require piece != null
     */
    private void capturePiece(ChessPiece piece)
    {
        assert piece != null : "Can't capture no piece";
        activePieces.remove(piece);
    }

    /**
     * Captures the given piece and applies the given move to the chess board.
     * The given move is stored in the movement history.
     * After the move has been applied, updates the active player's color to the opponent's color.
     * 
     * Note: The movement's destination and the captured piece's position may differ,
     * which is why this method takes the captured piece as an additional parameter.
     * 
     * Note: This method is not fit for applying castling moves. See {@link #applyCastlingMove(Move, Move)} for that.
     * 
     * @param move The move to apply
     * @param capturedPiece The piece to capture, or null if no piece has to be captured
     * @see Move
     * @see ChessPiece
     * 
     * @require move != null
     */
    public void applyMove(Move move, ChessPiece capturedPiece)
    {
        assert move != null;

        Position currPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        if (capturedPiece != null) capturePiece(capturedPiece);

        ChessPiece piece = getPieceAt(currPos);
        piece.setPosition(newPos);

        moveHistory.add(move);
        activePlayer = getOpponentColor();
    }

    /**
     * Applies a castling move to the chess board using the given king and rook moves.
     * After the move has been applied, updates the active player's color to the opponent's color.
     * Both the king's and the rook's moves are added to the movement history in said order.
     * 
     * @param kingMove The king's move
     * @param rookMove The rook's move
     * @see Move
     * 
     * @require kingMove != null
     * @require rookMove != null
     */
    public void applyCastlingMove(Move kingMove, Move rookMove)
    {
        assert kingMove != null;
        assert rookMove != null;

        Position kingCurrPos = kingMove.getCurrentPosition();
        Position kingNewPos = kingMove.getNewPosition();

        ChessPiece king = getPieceAt(kingCurrPos);
        king.setPosition(kingNewPos);

        Position rookCurrPos = rookMove.getCurrentPosition();
        Position rookNewPos = rookMove.getNewPosition();

        ChessPiece rook = getPieceAt(rookCurrPos);
        rook.setPosition(rookNewPos);

        moveHistory.add(kingMove);
        moveHistory.add(rookMove);
        activePlayer = getOpponentColor();
    }

    /**
     * Reverts the last recorded move from the internal movement history and adds the given previously captured piece back in.
     * After the move has been reverted, updates the active player's color to the opponent's color.
     * 
     * Note: This method is not fit for reverting castling moves. See {@link #undoLastCastlingMove()} for that.
     * 
     * @param capturedPiece The previously captured piece, or null if no piece was captured in the last move
     * @see ChessPiece
     */
    public void undoLastMove(ChessPiece capturedPiece)
    {
        Move undoMove = moveHistory.getLast();

        Position prevPos = undoMove.getCurrentPosition();
        Position currPos = undoMove.getNewPosition();

        ChessPiece movingPiece = getPieceAt(currPos);
        movingPiece.setPosition(prevPos);

        if (capturedPiece != null) activePieces.add(capturedPiece);

        moveHistory.removeLast();

        activePlayer = getOpponentColor();
    }

    /**
     * Reverts the last two recorded moves from the internal movement history.
     * After the moves have been reverted, updates the active player's color to the opponent's color.
     */
    public void undoLastCastlingMove()
    {
        for (int i = 0; i < 2; i++)
        {
            Move undoMove = moveHistory.getLast();

            Position prevPos = undoMove.getCurrentPosition();
            Position currPos = undoMove.getNewPosition();

            ChessPiece movingPiece = getPieceAt(currPos);
            movingPiece.setPosition(prevPos);

            moveHistory.removeLast();
        }

        activePlayer = getOpponentColor();
    }
}
