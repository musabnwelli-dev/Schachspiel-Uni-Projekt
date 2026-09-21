package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ChessRules defines the set of rules that each move in a game of chess can be evaluated against.
 */
public class ChessRules
{
    private ChessBoard board;

    /**
     * Initializes the chess rules with a given board.
     * 
     * @param board The chess board to check rules against.
     * 
     * @require board != null
     */
    public ChessRules(ChessBoard board)
    {
        assert board != null : "board should not be null";
        this.board = board;
    }

    /**
     * Checks if a move is valid according to basic chess rules, excluding king safety checks.
     * 
     * @param move The move to check.
     * @param movingColor The color of the player making the move.
     * @return true if the move is valid according to the rules, false otherwise.
     * 
     * @require move != null
     * @require movingColor != null
     * @see Move
     * @see PieceColor
     */
    public boolean checkRules(Move move, PieceColor movingColor)
    {
        assert move != null : "move should not be null";
        assert movingColor != null : "movingColor should not be null";

        // Not changing positions is not allowed
        if (move.getCurrentPosition()
            .isEqualTo(move.getNewPosition()))
        {
            return false;
        }

        // Current position has to have a piece ...
        ChessPiece piece = board.getPieceAt(move.getCurrentPosition());
        if (piece == null) return false;

        // ... and that piece should belong to the active player
        if (piece.getPieceColor() != movingColor) return false;

        // Piece specific movement rules have to be followed
        if (!piece.validateMove(move)) return false;

        // For most pieces, the movement path has to be clear
        PieceType type = piece.getPieceType();
        boolean pathClear = switch (type)
        {
        case PieceType.QUEEN, PieceType.BISHOP, PieceType.ROOK, PieceType.PAWN -> isPathClear(
                move);
        default -> true;
        };

        if (!pathClear) return false;

        // Can't capture own pieces
        ChessPiece targetPiece = board.getPieceAt(move.getNewPosition());
        if (targetPiece != null
                && piece.getPieceColor() == targetPiece.getPieceColor())
        {
            return false;
        }

        // Pawn can only ...
        if (type == PieceType.PAWN)
        {
            PawnMove moveType = getPawnMove(move);

            // (pawn move is correctly validated by validateMove;
            // in case something else goes wrong, we'll keep this for now)
            if (moveType == PawnMove.INVALID) return false;

            // ... move two steps on first move
            if (!pawnAtInitialPosition(piece) && moveType == PawnMove.DOUBLE)
            {
                return false;
            }

            // PAWN can either capture ...
            if (moveType == PawnMove.DIAGONAL)
            {
                // ... en passant
                if (targetPiece == null)
                {
                    if (!move.getNewPosition()
                        .isEqualTo(board.getEnPassantPosition()))
                    {
                        return false;
                    }
                }
                // ... or diagonally (without any additional conditions)
            }
            // Can't capture while moving straight
            else if ((moveType == PawnMove.SIMPLE
                    || moveType == PawnMove.DOUBLE) && targetPiece != null)
            {
                return false;
            }
        }

        if (type == PieceType.KING)
        {
            KingMove moveType = getKingMove(move);
            if (moveType == KingMove.INVALID) return false;

            if (moveType == KingMove.CASTLING && !isCastlingLegal(piece, move))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if a move is legal, including checking if it leaves the king in check.
     * 
     * @param move The move to check.
     * @return true if the move is legal, false otherwise.
     * 
     * @require move != null
     * @see Move
     */
    public boolean isMoveLegal(Move move)
    {
        assert move != null : "move should not be null";

        if (!checkRules(move, board.getActivePlayer()))
        {
            return false;
        }

        ChessPiece king = board.getActiveKing();
        if (king == null)
            throw new InternalError("King should always be there.");

        // simulate move + capture
        ChessPiece capturedPiece = board.getPieceAt(move.getNewPosition());

        board.applyMove(move, capturedPiece);

        // check if king is in check
        boolean kingInCheck = isInCheck(king);

        // undo move + capture
        board.undoLastMove(capturedPiece);

        if (kingInCheck) return false;

        return true;
    }

    /**
     * Returns the opposite color of the given color.
     * 
     * @param color The color to invert.
     * @return The opposite color.
     * 
     * @require color != null
     */
    private PieceColor otherColor(PieceColor color)
    {
        assert color != null : "color should not be null";
        return (color == PieceColor.WHITE) ? PieceColor.BLACK
                : PieceColor.WHITE;
    }

    /**
     * Generates all legal moves for a given piece.
     * 
     * @param piece The piece for which to generate moves.
     * @return A list of legal moves.
     * 
     * @require piece != null
     * @see ChessPiece
     * @see Move
     */
    public List<Move> generateLegalMovesFor(ChessPiece piece)
    {
        assert piece != null : "piece should not be null";

        List<Move> legalMoves = new ArrayList<Move>();

        for (int x = 1; x <= 8; x++)
        {
            for (int y = 1; y <= 8; y++)
            {
                Move move = new Move(piece.getPosition(), new Position(x, y));
                if (isMoveLegal(move))
                {
                    legalMoves.add(move);
                }
            }
        }

        return legalMoves;
    }

    /**
     * Checks if the given king is in check.
     * 
     * @param king The king to check.
     * @return true if the king is in check, false otherwise.
     * 
     * @require king != null
     * @see ChessPiece
     */
    private boolean isInCheck(ChessPiece king)
    {
        assert king != null : "king should not be null";
        PieceColor opponentColor = otherColor(king.getPieceColor());
        List<ChessPiece> activePieces = board.getActivePieces();
        List<ChessPiece> opponentPieces = activePieces.stream()
            .filter(p -> p.getPieceColor() == opponentColor)
            .toList();

        for (ChessPiece piece : opponentPieces)
        {
            Move finalMove = new Move(piece.getPosition(), king.getPosition());
            if (checkRules(finalMove, opponentColor))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the active player's king is in check.
     * 
     * @return The state of check, or null if not in check.
     * @see GameState
     */
    public GameState checkForCheck()
    {
        ChessPiece king = board.getActiveKing();
        if (!isInCheck(king)) return null;

        if (board.getActivePlayer() == PieceColor.WHITE)
        {
            return GameState.WHITE_CHECK;
        }
        else
            return GameState.BLACK_CHECK;
    }

    /**
     * Checks if the active player is in checkmate.
     * 
     * @return The state of checkmate, or null if not in checkmate.
     * @see GameState
     */
    public GameState checkForCheckmate()
    {
        List<ChessPiece> activePieces = board.getActivePieces();
        List<ChessPiece> movingPieces = activePieces.stream()
            .filter(p -> p.getPieceColor() == board.getActivePlayer())
            .toList();
        List<Move> legalMoves = new ArrayList<Move>();

        // generate all legal moves that would get the king out of check
        for (ChessPiece piece : movingPieces)
        {
            legalMoves.addAll(generateLegalMovesFor(piece));
        }

        GameState newState = null;

        if (legalMoves.isEmpty())
        {
            if (board.getActivePlayer() == PieceColor.WHITE)
            {
                newState = GameState.WHITE_CHECKMATE;
            }
            else
                newState = GameState.BLACK_CHECKMATE;
        }

        return newState;
    }

    /**
     * Checks if the path of a move is clear of other pieces.
     * 
     * @param move The move to check.
     * @return true if the path is clear, false otherwise.
     * 
     * @require move != null
     * @see Move
     */
    private boolean isPathClear(Move move)
    {
        assert move != null : "move should not be null";
        Position currPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        int probeX = currPos.getX();
        int probeY = currPos.getY();

        Position probePos = new Position(probeX, probeY);

        while (!probePos.isEqualTo(newPos))
        {
            if (probeX < newPos.getX())
                probeX++;
            else if (probeX > newPos.getX()) probeX--;

            if (probeY < newPos.getY())
                probeY++;
            else if (probeY > newPos.getY()) probeY--;

            // as long as we are not on new position, probe position should not contain a piece
            probePos = new Position(probeX, probeY);
            if (!probePos.isEqualTo(newPos)
                    && board.getPieceAt(probePos) != null)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a pawn is at its initial starting position.
     * 
     * @param piece The pawn to check.
     * @return true if the pawn is at its initial position, false otherwise.
     * 
     * @require piece != null
     * @see ChessPiece
     */
    private boolean pawnAtInitialPosition(ChessPiece piece)
    {
        assert piece != null : "piece should not be null";
        assert piece
            .getPieceType() == PieceType.PAWN : "Piece should be a pawn";

        PieceColor c = piece.getPieceColor();
        Position p = piece.getPosition();

        // pawn should be in the row it started in
        if (c == PieceColor.WHITE && p.getY() == 2) return true;
        if (c == PieceColor.BLACK && p.getY() == 7) return true;

        return false;
    }

    /**
     * Returns the position a pawn can capture via en passant based on the given move.
     * 
     * @param move The move to use.
     * @return The en passant position.
     * 
     * @require move != null
     * @see Move
     * @see Position
     */
    public Position getEnPassantPosition(Move move)
    {
        assert move != null : "move should not be null";

        Position currentPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        // the capture position is behind the pawn,
        // so -1 when moving down (black) or +1 when moving up (white)
        int yDirectionOffset = currentPos.getY() > newPos.getY() ? -1 : 1;

        return new Position(currentPos.getX(),
                currentPos.getY() + yDirectionOffset);
    }

    /**
     * Returns the pawn piece that would be captured during an en passant move.
     * 
     * @param move The move to check.
     * @return The captured pawn piece.
     * 
     * @require move != null
     * @see Move
     * @see ChessPiece
     */
    public ChessPiece getPieceCapturedEnPassant(Move move)
    {
        assert move != null : "move should not be null";

        Position currentPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        // since the pawn is moving diagonally behind the target pawn,
        // we can just remove the change on the y axis to get the capture position
        Position pos = new Position(newPos.getX(), currentPos.getY());

        ChessPiece capturedPiece = board.getPieceAt(pos);
        if (capturedPiece == null
                || capturedPiece.getPieceType() != PieceType.PAWN)
        {
            throw new InternalError(
                    "The captured piece for an en passant capture should always exist and be a pawn.");
        }

        return capturedPiece;
    }

    /**
     * Determines the type of pawn movement for a given move.
     * 
     * @param move The move to evaluate.
     * @return The {@link PawnMove} type.
     * 
     * @require move != null
     * @see Move
     * @see PawnMove
     */
    public PawnMove getPawnMove(Move move)
    {
        assert move != null : "move should not be null";

        Position currPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        int movX = Math.abs(currPos.getX() - newPos.getX());
        int movY = Math.abs(currPos.getY() - newPos.getY());

        if (movX == 0 && movY == 1) return PawnMove.SIMPLE;
        if (movX == 0 && movY == 2) return PawnMove.DOUBLE;
        if (movX == 1 && movY == 1) return PawnMove.DIAGONAL;

        return PawnMove.INVALID;
    }

    /**
     * Determines the type of king movement for a given move.
     * 
     * @param move The move to evaluate.
     * @return The {@link KingMove} type.
     * 
     * @require move != null
     * @see Move
     * @see KingMove
     */
    public KingMove getKingMove(Move move)
    {
        assert move != null : "move should not be null";

        Position currPos = move.getCurrentPosition();
        Position newPos = move.getNewPosition();

        int movX = Math.abs(currPos.getX() - newPos.getX());
        int movY = Math.abs(currPos.getY() - newPos.getY());

        if ((movY == 1 || movY == 0) && movX == 1) return KingMove.SIMPLE;
        if (movY == 1 && (movX == 1 || movX == 0)) return KingMove.SIMPLE;
        if (movY == 0 && movX == 2) return KingMove.CASTLING;

        return KingMove.INVALID;
    }

    /**
     * Determines the castling variant associated with a given move.
     * 
     * @param move The move to evaluate.
     * @return The {@link CastlingRights} variant, or null if no castling is possible via this move.
     * 
     * @require move != null
     * @see Move
     * @see CastlingRights
     */
    public CastlingRights getCastlingVariant(Move move)
    {
        assert move != null : "move should not be null";

        Position newPos = move.getNewPosition();

        Position whiteQueenSide = new Position(3, 1);
        Position whiteKingSide = new Position(7, 1);
        Position blackQueenSide = new Position(3, 8);
        Position blackKingSide = new Position(7, 8);

        if (newPos.equals(whiteQueenSide))
            return CastlingRights.WHITE_QUEEN_SIDE;
        if (newPos.equals(whiteKingSide)) return CastlingRights.WHITE_KING_SIDE;
        if (newPos.equals(blackQueenSide))
            return CastlingRights.BLACK_QUEEN_SIDE;
        if (newPos.equals(blackKingSide)) return CastlingRights.BLACK_KING_SIDE;

        return null;
    }

    /**
     * Checks if a castling move is legal.
     * 
     * @param king The king making the move.
     * @param move The move to check.
     * @return true if the castling move is legal, false otherwise.
     * 
     * @require king != null
     * @require move != null
     * @see ChessPiece
     * @see Move
     * @see CastlingRights
     */
    private boolean isCastlingLegal(ChessPiece king, Move move)
    {
        assert king != null : "king should not be null";
        assert move != null : "move should not be null";

        Set<CastlingRights> remainingRights = board.getCastlingRights();

        if (remainingRights.isEmpty()) return false;
        if (!isPathClear(move)) return false;

        // if the right remains, both king and rook have not moved
        CastlingRights variant = getCastlingVariant(move);
        if (variant == null) return false;
        if (!remainingRights.contains(variant)) return false;

        // the king should not be in check for any position he is on or passes over

        Position currentPos = move.getCurrentPosition();
        Position middlePos = new Position(currentPos.getX() + (List
            .of(CastlingRights.WHITE_QUEEN_SIDE,
                    CastlingRights.BLACK_QUEEN_SIDE)
            .contains(variant) ? -1 : 1), currentPos.getY());

        if (isInCheck(king)) return false;

        // middle and end
        for (Move m : List.of(new Move(currentPos, middlePos), move))
        {
            board.applyMove(m, null);
            boolean kingInCheck = isInCheck(king);
            board.undoLastMove(null);
            if (kingInCheck) return false;
        }

        return true;
    }
}
