package model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChessGameTest
{
    @Test
    void newGameExposesStartingStateAndLegalMoves()
    {
        ChessGame game = new ChessGame();
        ChessPiece pawn = findPiece(game, "e2");
        assertNotNull(pawn);

        assertAll(() -> assertEquals(32, game.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.WHITE, game.getActivePlayer()),
                () -> assertEquals(GameState.RUNNING, game.getGameState()),
                () -> assertEquals(Set.of(position("e3"), position("e4")),
                        Set.copyOf(game.getLegalPositionsForPiece(pawn))));
    }

    @Test
    void appliesOnlyLegalMoves()
    {
        ChessGame game = new ChessGame();
        ChessPiece pawn = findPiece(game, "e2");
        assertNotNull(pawn);

        assertFalse(game.applyMove(move("a7", "a6")));
        assertEquals(PieceColor.WHITE, game.getActivePlayer());

        assertTrue(game.applyMove(move("e2", "e4")));
        assertAll(() -> assertSame(pawn, findPiece(game, "e4")),
                () -> assertNull(findPiece(game, "e2")),
                () -> assertEquals(PieceColor.BLACK, game.getActivePlayer()),
                () -> assertEquals(GameState.RUNNING, game.getGameState()));
    }

    @Test
    void resetRestoresStartingPosition()
    {
        ChessGame game = new ChessGame();
        game.applyMove(move("e2", "e4"));

        game.resetBoard();

        assertAll(() -> assertEquals(32, game.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.WHITE, game.getActivePlayer()),
                () -> assertNotNull(findPiece(game, "e2")),
                () -> assertNull(findPiece(game, "e4")));
    }

    @Test
    void loadsAndSavesFen(@TempDir Path tempDirectory) throws IOException
    {
        String fen = "4k3/8/8/8/8/8/8/4K3 b - - 0 1";
        Path input = tempDirectory.resolve("input.fen");
        Path output = tempDirectory.resolve("output.fen");
        Files.writeString(input, fen);
        ChessGame game = new ChessGame();

        assertTrue(game.loadBoard(input.toString()));
        assertAll(() -> assertEquals(2, game.getActivePieces()
            .size()),
                () -> assertEquals(PieceColor.BLACK, game.getActivePlayer()));

        game.saveBoard(output.toString());
        assertEquals(fen, Files.readString(output)
            .strip());
    }

    private static ChessPiece findPiece(ChessGame game, String square)
    {
        Position position = position(square);
        return game.getActivePieces()
            .stream()
            .filter(piece -> piece.getPosition()
                .isEqualTo(position))
            .findFirst()
            .orElse(null);
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
