package model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * MoveProcessor handles the application of valid chess moves to a ChessBoard,
 * including special moves like castling and en passant, and updating the game state.
 */
public class MoveProcessor
{
    private ChessBoard board;
    private ChessRules rules;

    /**
     * Initializes the move processor with the given board and rules.
     * 
     * @param board The chess board to operate on.
     * @param rules The chess rules to apply.
     * 
     * @require board != null
     * @require rules != null
     */
    public MoveProcessor(ChessBoard board, ChessRules rules)
    {
        assert board != null : "board should not be null";
        assert rules != null : "rules should not be null";
        this.board = board;
        this.rules = rules;
    }

    /**
     * Applies a valid chess move to the board, handling special moves like castling 
     * and en passant, and updating the game state.
     * 
     * @param move The valid move to be applied.
     * 
     * @require move != null
     * @see Move
     */
    public void applyValidMove(Move move)
    {
        assert move != null : "move should not be null";

        ChessPiece movingPiece = board.getPieceAt(move.getCurrentPosition());
        ChessPiece capturedPiece = null;

        if (movingPiece.getPieceType() == PieceType.KING
                && rules.getKingMove(move) == KingMove.CASTLING)
        {
            processCastlingMove(move);
        }
        else
        {
            capturedPiece = getCapturedPiece(move, movingPiece);
            board.applyMove(move, capturedPiece);
        }

        updateEnPassantPosition(move, movingPiece);
        updateCastlingRights(move, movingPiece, capturedPiece);
        updateGameState();
    }

    /**
     * Gets the captured piece for a given move.
     * 
     * @param move The move to evaluate.
     * @param movingPiece The piece making the move.
     * @return The captured piece, or null if no piece was captured.
     * 
     * @require move != null
     * @require movingPiece != null
     * @see Move
     * @see ChessPiece
     */
    private ChessPiece getCapturedPiece(Move move, ChessPiece movingPiece)
    {
        assert move != null : "move should not be null";
        assert movingPiece != null : "movingPiece should not be null";
        ChessPiece capturedPiece = null;
        ChessPiece targetPiece = board.getPieceAt(move.getNewPosition());

        if (movingPiece.getPieceType() == PieceType.PAWN
                && rules.getPawnMove(move) == PawnMove.DIAGONAL)
        {
            if (targetPiece != null)
                capturedPiece = targetPiece;
            else
                capturedPiece = rules.getPieceCapturedEnPassant(move);
        }
        else
        {
            capturedPiece = targetPiece;
        }

        return capturedPiece;
    }

    /**
     * Updates the game state based on the current board configuration.
     * 
     * @see GameState
     */
    private void updateGameState()
    {
        GameState newState = GameState.RUNNING;
        GameState stateAfterCheck;

        stateAfterCheck = rules.checkForCheck();
        if (stateAfterCheck != null) newState = stateAfterCheck;

        if (newState == GameState.BLACK_CHECK
                || newState == GameState.WHITE_CHECK)
        {
            stateAfterCheck = rules.checkForCheckmate();
            if (stateAfterCheck != null) newState = stateAfterCheck;
        }

        board.setState(newState);
    }

    /**
     * Updates the en passant position on the board.
     * 
     * @param move The move to apply.
     * @param movingPiece The piece making the move.
     * 
     * @require move != null
     * @require movingPiece != null
     * @see Move
     * @see ChessPiece
     */
    private void updateEnPassantPosition(Move move, ChessPiece movingPiece)
    {
        assert move != null : "move should not be null";
        assert movingPiece != null : "movingPiece should not be null";
        if ((movingPiece.getPieceType() == PieceType.PAWN)
                && (rules.getPawnMove(move) == PawnMove.DOUBLE))
        {
            board.setEnPassantPosition(rules.getEnPassantPosition(move));
            return;
        }

        board.setEnPassantPosition(null);
    }

    /**
     * Processes a castling move.
     * 
     * @param move The move to process.
     * 
     * @require move != null
     * @see Move
     */
    private void processCastlingMove(Move move)
    {
        assert move != null : "move should not be null";
        Move kingMove = move;
        CastlingRights variant = rules.getCastlingVariant(kingMove);
        Move rookMove = getRookMoveForCastling(variant);
        board.applyCastlingMove(kingMove, rookMove);
    }

    /**
     * Gets the rook move for a given castling variant.
     * 
     * @param variant The castling variant.
     * @return The rook move, or null if the variant is invalid.
     * 
     * @require variant != null
     * @see CastlingRights
     */
    private Move getRookMoveForCastling(CastlingRights variant)
    {
        assert variant != null : "variant should not be null";
        Position currentPos;
        Position newPos;

        switch (variant)
        {
        case WHITE_QUEEN_SIDE:
            currentPos = new Position(1, 1);
            newPos = new Position(4, 1);
            break;
        case WHITE_KING_SIDE:
            currentPos = new Position(8, 1);
            newPos = new Position(6, 1);
            break;
        case BLACK_QUEEN_SIDE:
            currentPos = new Position(1, 8);
            newPos = new Position(4, 8);
            break;
        case BLACK_KING_SIDE:
            currentPos = new Position(8, 8);
            newPos = new Position(6, 8);
            break;
        default:
            return null;
        }

        return new Move(currentPos, newPos);
    }

    /**
     * Updates the castling rights on the board.
     * 
     * @param move The move to apply.
     * @param movingPiece The piece making the move.
     * @param capturedPiece The captured piece, or null if there was no capture.
     * 
     * @require move != null
     * @require movingPiece != null
     * @see Move
     * @see ChessPiece
     */
    private void updateCastlingRights(Move move, ChessPiece movingPiece,
            ChessPiece capturedPiece)
    {
        assert move != null : "move should not be null";
        assert movingPiece != null : "movingPiece should not be null";
        if (board.getCastlingRights()
            .isEmpty()) return;

        List<Position> queenSideRooks = List.of(new Position(1, 1),
                new Position(1, 8));
        List<Position> kingSideRooks = List.of(new Position(8, 1),
                new Position(8, 8));

        Set<CastlingRights> rightsToRemove = EnumSet
            .noneOf(CastlingRights.class);

        PieceType type = movingPiece.getPieceType();
        PieceColor color = movingPiece.getPieceColor();
        Position position = move.getCurrentPosition();

        switch (type)
        {
        case ROOK:

            if (queenSideRooks.contains(position))
            {
                if (color == PieceColor.WHITE)
                {
                    rightsToRemove.add(CastlingRights.WHITE_QUEEN_SIDE);
                }
                else
                    rightsToRemove.add(CastlingRights.BLACK_QUEEN_SIDE);
            }
            if (kingSideRooks.contains(position))
            {
                if (color == PieceColor.WHITE)
                {
                    rightsToRemove.add(CastlingRights.WHITE_KING_SIDE);
                }
                else
                    rightsToRemove.add(CastlingRights.BLACK_KING_SIDE);
            }
            break;
        case KING:
            if (color == PieceColor.WHITE)
            {
                rightsToRemove.addAll(List.of(CastlingRights.WHITE_KING_SIDE,
                        CastlingRights.WHITE_QUEEN_SIDE));
            }
            else
            {
                rightsToRemove.addAll(List.of(CastlingRights.BLACK_KING_SIDE,
                        CastlingRights.BLACK_QUEEN_SIDE));
            }
            break;
        default:
            break;
        }

        if (capturedPiece != null
                && capturedPiece.getPieceType() == PieceType.ROOK)
        {
            Position capturedPosition = capturedPiece.getPosition();
            PieceColor capturedColor = capturedPiece.getPieceColor();

            if (queenSideRooks.contains(capturedPosition))
            {
                rightsToRemove.add(capturedColor == PieceColor.WHITE
                        ? CastlingRights.WHITE_QUEEN_SIDE
                        : CastlingRights.BLACK_QUEEN_SIDE);
            }
            if (kingSideRooks.contains(capturedPosition))
            {
                rightsToRemove.add(capturedColor == PieceColor.WHITE
                        ? CastlingRights.WHITE_KING_SIDE
                        : CastlingRights.BLACK_KING_SIDE);
            }
        }

        board.removeCastlingRights(rightsToRemove);
    }
}
