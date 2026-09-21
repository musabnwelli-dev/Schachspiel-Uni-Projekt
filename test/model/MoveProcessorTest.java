package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MoveProcessorTest
{
    @Test
    void doublePawnMoveSetsEnPassantSquare()
    {
        ChessBoard board = board("4k3/8/8/8/8/8/4P3/4K3 w - - 0 1");

        processor(board).applyValidMove(move("e2", "e4"));

        assertEquals(position("e3"), board.getEnPassantPosition());
    }

    @Test
    void ordinaryCaptureRemovesTargetAndClearsEnPassantSquare()
    {
        ChessBoard board = board("4k3/8/8/3p4/4P3/8/8/4K3 w - d6 0 1");
        ChessPiece pawn = board.getPieceAt(position("e4"));

        processor(board).applyValidMove(move("e4", "d5"));

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("d5"))),
                () -> assertEquals(3, board.getActivePieces()
                    .size()),
                () -> assertNull(board.getEnPassantPosition()),
                () -> assertEquals(PieceColor.BLACK, board.getActivePlayer()));
    }

    @Test
    void enPassantRemovesPawnBesideStartingSquare()
    {
        ChessBoard board = board("4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 1");
        ChessPiece pawn = board.getPieceAt(position("e5"));

        processor(board).applyValidMove(move("e5", "d6"));

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("d6"))),
                () -> assertNull(board.getPieceAt(position("d5"))),
                () -> assertEquals(3, board.getActivePieces()
                    .size()));
    }

    @Test
    void castlingMovesBothPiecesAndRemovesKingsRights()
    {
        ChessBoard board = board("4k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1");

        processor(board).applyValidMove(move("e1", "g1"));

        assertAll(
                () -> assertEquals(PieceType.KING,
                        board.getPieceAt(position("g1"))
                            .getPieceType()),
                () -> assertEquals(PieceType.ROOK,
                        board.getPieceAt(position("f1"))
                            .getPieceType()),
                () -> assertEquals(2, board.getMoveHistory()
                    .size()),
                () -> assertFalse(board.getCastlingRights()
                    .contains(CastlingRights.WHITE_KING_SIDE)),
                () -> assertFalse(board.getCastlingRights()
                    .contains(CastlingRights.WHITE_QUEEN_SIDE)),
                () -> assertTrue(board.getCastlingRights()
                    .contains(CastlingRights.BLACK_KING_SIDE)));
    }

    @Test
    void movingRookRemovesOnlyItsCastlingRight()
    {
        ChessBoard board = board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        processor(board).applyValidMove(move("h1", "h2"));

        assertAll(() -> assertFalse(board.getCastlingRights()
            .contains(CastlingRights.WHITE_KING_SIDE)),
                () -> assertTrue(board.getCastlingRights()
                    .contains(CastlingRights.WHITE_QUEEN_SIDE)),
                () -> assertTrue(board.getCastlingRights()
                    .contains(CastlingRights.BLACK_KING_SIDE)),
                () -> assertTrue(board.getCastlingRights()
                    .contains(CastlingRights.BLACK_QUEEN_SIDE)));
    }

    @Test
    void capturingRookRemovesCapturedSidesCastlingRight()
    {
        ChessBoard board = board("r3k3/8/8/8/8/8/8/R3K3 w Qq - 0 1");

        processor(board).applyValidMove(move("a1", "a8"));

        assertAll(() -> assertFalse(board.getCastlingRights()
            .contains(CastlingRights.WHITE_QUEEN_SIDE)),
                () -> assertFalse(board.getCastlingRights()
                    .contains(CastlingRights.BLACK_QUEEN_SIDE)));
    }

    @Test
    void moveUpdatesStateToCheckmate()
    {
        ChessBoard board = board("7k/5Q2/5K2/8/8/8/8/8 w - - 0 1");

        processor(board).applyValidMove(move("f7", "g7"));

        assertEquals(GameState.BLACK_CHECKMATE, board.getState());
    }

    private static ChessBoard board(String fen)
    {
        return new ChessBoard(fen);
    }

    private static MoveProcessor processor(ChessBoard board)
    {
        return new MoveProcessor(board, new ChessRules(board));
    }

    private static Move move(String from, String to)
    {
        return new Move(position(from), position(to));
    }

    private static Position position(String square)
    {
        return new Position(square.charAt(0) - 'a' + 1, square.charAt(1) - '0');
    }
}
