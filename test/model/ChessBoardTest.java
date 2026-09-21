package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChessBoardTest
{
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private ChessBoard board;

    @BeforeEach
    void setUp()
    {
        board = new ChessBoard();
    }

    @Test
    void defaultBoardHasCompleteStartingState()
    {
        PieceType[] backRank = {PieceType.ROOK, PieceType.KNIGHT,
                PieceType.BISHOP, PieceType.QUEEN, PieceType.KING,
                PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        assertAll(() -> assertEquals(GameState.RUNNING, board.getState()),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()),
                () -> assertEquals(PieceColor.BLACK, board.getOpponentColor()),
                () -> assertEquals(32, board.getActivePieces()
                    .size()),
                () -> assertEquals(Set.of(CastlingRights.values()),
                        board.getCastlingRights()),
                () -> assertNull(board.getEnPassantPosition()),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()),
                () -> assertNull(board.getImportError()));

        for (int file = 1; file <= 8; file++)
        {
            assertPiece(position(file, 1), backRank[file - 1],
                    PieceColor.WHITE);
            assertPiece(position(file, 2), PieceType.PAWN, PieceColor.WHITE);
            assertPiece(position(file, 7), PieceType.PAWN, PieceColor.BLACK);
            assertPiece(position(file, 8), backRank[file - 1],
                    PieceColor.BLACK);
        }
    }

    @Test
    void validFenInitializesAllStoredState()
    {
        board = new ChessBoard("k7/8/8/8/4P3/8/8/7K b Q e3 0 1");

        assertAll(() -> assertEquals(3, board.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.BLACK, board.getActivePlayer()),
                () -> assertEquals(PieceColor.WHITE, board.getOpponentColor()),
                () -> assertEquals(Set.of(CastlingRights.WHITE_QUEEN_SIDE),
                        board.getCastlingRights()),
                () -> assertEquals(position("e3"),
                        board.getEnPassantPosition()),
                () -> assertEquals(PieceColor.BLACK, board.getActiveKing()
                    .getPieceColor()),
                () -> assertNull(board.getImportError()));
    }

    @Test
    void invalidFenCreatesSafeFallbackState()
    {
        board = new ChessBoard("invalid");

        assertAll(() -> assertEquals(GameState.IMPORT_FAILED, board.getState()),
                () -> assertTrue(board.getActivePieces()
                    .isEmpty()),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()),
                () -> assertTrue(board.getCastlingRights()
                    .isEmpty()),
                () -> assertNull(board.getEnPassantPosition()),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()),
                () -> assertTrue(board.getImportError()
                    .contains("DataFormatException")));
    }

    @Test
    void exportsCurrentStateAsFen()
    {
        String customFen = "rnbqkbnr/ppppp2p/5p2/6p1/5P2/4P3/PPPP2PP/RNBQKBNR w KQkq - 0 1";

        assertEquals(STARTING_FEN, board.exportStateToFenString());
        assertEquals(customFen,
                new ChessBoard(customFen).exportStateToFenString());
    }

    @Test
    void exposedCollectionsCannotBeModified()
    {
        List<ChessPiece> pieces = board.getActivePieces();
        List<Move> history = board.getMoveHistory();

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> pieces.removeFirst()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> history.add(move("a2", "a3"))));
    }

    @Test
    void stateAndEnPassantSquareCanBeUpdatedAndCleared()
    {
        board.setState(GameState.WHITE_CHECK);
        board.setEnPassantPosition(position("e3"));

        assertAll(() -> assertEquals(GameState.WHITE_CHECK, board.getState()),
                () -> assertEquals(position("e3"),
                        board.getEnPassantPosition()));

        board.setState(GameState.RUNNING);
        board.setEnPassantPosition(null);

        assertAll(() -> assertEquals(GameState.RUNNING, board.getState()),
                () -> assertNull(board.getEnPassantPosition()));
    }

    @Test
    void castlingRightsCanBeRemoved()
    {
        board.removeCastlingRights(Set.of(CastlingRights.BLACK_KING_SIDE,
                CastlingRights.BLACK_QUEEN_SIDE));

        assertEquals(
                Set.of(CastlingRights.WHITE_KING_SIDE,
                        CastlingRights.WHITE_QUEEN_SIDE),
                board.getCastlingRights());
    }

    @Test
    void pieceLookupAndActiveKingReflectBoardState()
    {
        ChessPiece king = board.getActiveKing();

        assertAll(() -> assertSame(king, board.getPieceAt(position("e1"))),
                () -> assertEquals(PieceType.KING, king.getPieceType()),
                () -> assertEquals(PieceColor.WHITE, king.getPieceColor()),
                () -> assertNull(board.getPieceAt(position("e4"))));
    }

    @Test
    void applyingAndUndoingMoveRestoresBoardAndTurn()
    {
        Move move = move("e2", "e4");
        ChessPiece pawn = board.getPieceAt(position("e2"));

        board.applyMove(move, null);

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("e4"))),
                () -> assertNull(board.getPieceAt(position("e2"))),
                () -> assertEquals(PieceColor.BLACK, board.getActivePlayer()),
                () -> assertEquals(List.of(move), board.getMoveHistory()),
                () -> assertEquals(GameState.RUNNING, board.getState()));

        board.undoLastMove(null);

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("e2"))),
                () -> assertNull(board.getPieceAt(position("e4"))),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()));
    }

    @Test
    void applyingAndUndoingCaptureRestoresBothPieces()
    {
        board = new ChessBoard("4k3/8/8/6p1/7P/8/8/4K3 w - - 0 1");
        Move move = move("h4", "g5");
        ChessPiece pawn = board.getPieceAt(position("h4"));
        ChessPiece capturedPawn = board.getPieceAt(position("g5"));

        board.applyMove(move, capturedPawn);

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("g5"))),
                () -> assertFalse(board.getActivePieces()
                    .contains(capturedPawn)),
                () -> assertEquals(3, board.getActivePieces()
                    .size()));

        board.undoLastMove(capturedPawn);

        assertAll(() -> assertSame(pawn, board.getPieceAt(position("h4"))),
                () -> assertSame(capturedPawn,
                        board.getPieceAt(position("g5"))),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()));
    }

    @Test
    void historySupportsMultipleMovesAndLifoUndo()
    {
        Move whiteMove = move("e2", "e4");
        Move blackMove = move("d7", "d5");

        board.applyMove(whiteMove, null);
        board.applyMove(blackMove, null);
        assertEquals(List.of(whiteMove, blackMove), board.getMoveHistory());

        board.undoLastMove(null);
        assertAll(
                () -> assertEquals(List.of(whiteMove), board.getMoveHistory()),
                () -> assertNotNull(board.getPieceAt(position("d7"))),
                () -> assertEquals(PieceColor.BLACK, board.getActivePlayer()));
    }

    @Test
    void applyingAndUndoingCastlingMovesKingAndRook()
    {
        assertCastling("e1", "g1", "h1", "f1");
        assertCastling("e1", "c1", "a1", "d1");
    }

    private void assertCastling(String kingFrom, String kingTo, String rookFrom,
            String rookTo)
    {
        board = new ChessBoard(
                "r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
        Move kingMove = move(kingFrom, kingTo);
        Move rookMove = move(rookFrom, rookTo);

        board.applyCastlingMove(kingMove, rookMove);

        assertAll(
                () -> assertEquals(PieceType.KING,
                        board.getPieceAt(position(kingTo))
                            .getPieceType()),
                () -> assertEquals(PieceType.ROOK,
                        board.getPieceAt(position(rookTo))
                            .getPieceType()),
                () -> assertNull(board.getPieceAt(position(kingFrom))),
                () -> assertNull(board.getPieceAt(position(rookFrom))),
                () -> assertEquals(List.of(kingMove, rookMove),
                        board.getMoveHistory()),
                () -> assertEquals(PieceColor.BLACK, board.getActivePlayer()));

        board.undoLastCastlingMove();

        assertAll(
                () -> assertEquals(PieceType.KING,
                        board.getPieceAt(position(kingFrom))
                            .getPieceType()),
                () -> assertEquals(PieceType.ROOK,
                        board.getPieceAt(position(rookFrom))
                            .getPieceType()),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()));
    }

    private void assertPiece(Position position, PieceType type,
            PieceColor color)
    {
        ChessPiece piece = board.getPieceAt(position);
        assertNotNull(piece, "piece at " + position);
        assertAll("piece at " + position,
                () -> assertEquals(type, piece.getPieceType()),
                () -> assertEquals(color, piece.getPieceColor()));
    }

    private static Move move(String from, String to)
    {
        return new Move(position(from), position(to));
    }

    private static Position position(String square)
    {
        return position(square.charAt(0) - 'a' + 1, square.charAt(1) - '0');
    }

    private static Position position(int file, int rank)
    {
        return new Position(file, rank);
    }
}
