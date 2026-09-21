package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Test;

class FenStateConverterTest
{
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final String KINGS_ONLY = "4k3/8/8/8/8/8/8/4K3";

    @Test
    void parsesCompleteStartingState() throws DataFormatException
    {
        FenState state = FenStateConverter.fromString(STARTING_FEN);

        assertAll(() -> assertEquals(32, state.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.WHITE, state.getActiveColor()),
                () -> assertEquals(Set.of(CastlingRights.values()),
                        state.getCastlingAvailability()),
                () -> assertNull(state.getEnPassantSquare()),
                () -> assertEquals(0, state.getHalfmoveClock()),
                () -> assertEquals(1, state.getFullmoveNumber()));

        assertPiece(state, "a2", PieceType.PAWN, PieceColor.WHITE);
        assertPiece(state, "e1", PieceType.KING, PieceColor.WHITE);
        assertPiece(state, "d8", PieceType.QUEEN, PieceColor.BLACK);
        assertPiece(state, "e8", PieceType.KING, PieceColor.BLACK);
    }

    @Test
    void parsesMixedPiecesAndMetadata() throws DataFormatException
    {
        String fen = "r3k2r/1pppNppp/p7/8/4P3/5n2/PPPP1PPP/RNBQKB1R b Kq a6 42 5";
        FenState state = FenStateConverter.fromString(fen);

        assertAll(() -> assertEquals(27, state.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.BLACK, state.getActiveColor()),
                () -> assertEquals(
                        Set.of(CastlingRights.WHITE_KING_SIDE,
                                CastlingRights.BLACK_QUEEN_SIDE),
                        state.getCastlingAvailability()),
                () -> assertEquals(position("a6"), state.getEnPassantSquare()),
                () -> assertEquals(42, state.getHalfmoveClock()),
                () -> assertEquals(5, state.getFullmoveNumber()));

        assertPiece(state, "e7", PieceType.KNIGHT, PieceColor.WHITE);
        assertPiece(state, "f3", PieceType.KNIGHT, PieceColor.BLACK);
        assertPiece(state, "e4", PieceType.PAWN, PieceColor.WHITE);
    }

    @Test
    void parsesCastlingRightVariants() throws DataFormatException
    {
        assertCastling("K", Set.of(CastlingRights.WHITE_KING_SIDE));
        assertCastling("Q", Set.of(CastlingRights.WHITE_QUEEN_SIDE));
        assertCastling("k", Set.of(CastlingRights.BLACK_KING_SIDE));
        assertCastling("q", Set.of(CastlingRights.BLACK_QUEEN_SIDE));
        assertCastling("Kq", Set.of(CastlingRights.WHITE_KING_SIDE,
                CastlingRights.BLACK_QUEEN_SIDE));
        assertCastling("KQkq", Set.of(CastlingRights.values()));
        assertCastling("-", Set.of());
    }

    @Test
    void parsesValidEnPassantSquares() throws DataFormatException
    {
        for (String square : List.of("e3", "a6", "h3"))
        {
            FenState state = FenStateConverter
                .fromString(KINGS_ONLY + " w - " + square + " 0 1");
            assertEquals(position(square), state.getEnPassantSquare(), square);
        }

        FenState state = FenStateConverter
            .fromString(KINGS_ONLY + " w - - 0 1");
        assertNull(state.getEnPassantSquare());
    }

    @Test
    void trimsSurroundingWhitespace() throws DataFormatException
    {
        FenState state = FenStateConverter
            .fromString("  \t" + STARTING_FEN + " \n ");

        assertEquals(32, state.getActivePieces()
            .size());
    }

    @Test
    void rejectsMalformedFenStructure()
    {
        assertInvalidFens(List.of(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq",
                STARTING_FEN + " extra",
                "rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                " w KQkq - 0 1", "8k/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    void rejectsInvalidMetadata()
    {
        assertInvalidFens(List.of(KINGS_ONLY + " x - - 0 1",
                KINGS_ONLY + " W - - 0 1", KINGS_ONLY + " w X - 0 1",
                KINGS_ONLY + " w - e1 0 1", KINGS_ONLY + " w - e2 0 1",
                KINGS_ONLY + " w - e7 0 1", KINGS_ONLY + " w - e8 0 1",
                KINGS_ONLY + " w - - -1 1", KINGS_ONLY + " w - - abc 1",
                KINGS_ONLY + " w - - 0 0", KINGS_ONLY + " w - - 0 -1",
                KINGS_ONLY + " w - - 0 xyz"));
    }

    @Test
    void roundTripPreservesCompleteAndSparseStates() throws DataFormatException
    {
        List<String> fens = List.of(STARTING_FEN,
                "r3k2r/1pppNppp/p7/8/4P3/5n2/PPPP1PPP/RNBQKB1R b Kq a6 42 5",
                "8/8/8/8/8/8/8/K7 b - - 12 34");

        for (String fen : fens)
        {
            FenState state = FenStateConverter.fromString(fen);
            int pieceCount = state.getActivePieces()
                .size();

            assertEquals(fen, FenStateConverter.toString(state), fen);
            assertEquals(pieceCount, state.getActivePieces()
                .size(), "conversion must not mutate state");
        }
    }

    private static void assertCastling(String notation,
            Set<CastlingRights> expected) throws DataFormatException
    {
        FenState state = FenStateConverter
            .fromString(KINGS_ONLY + " w " + notation + " - 0 1");
        assertEquals(expected, state.getCastlingAvailability(), notation);
    }

    private static void assertInvalidFens(List<String> invalidFens)
    {
        for (String fen : invalidFens)
        {
            assertThrows(DataFormatException.class,
                    () -> FenStateConverter.fromString(fen), fen);
        }
    }

    private static void assertPiece(FenState state, String square,
            PieceType type, PieceColor color)
    {
        ChessPiece piece = findPiece(state, position(square));
        assertNotNull(piece, "piece at " + square);
        assertAll("piece at " + square,
                () -> assertEquals(type, piece.getPieceType()),
                () -> assertEquals(color, piece.getPieceColor()));
    }

    private static ChessPiece findPiece(FenState state, Position position)
    {
        return state.getActivePieces()
            .stream()
            .filter(piece -> piece.getPosition()
                .isEqualTo(position))
            .findFirst()
            .orElse(null);
    }

    private static Position position(String square)
    {
        return new Position(square.charAt(0) - 'a' + 1, square.charAt(1) - '0');
    }
}
