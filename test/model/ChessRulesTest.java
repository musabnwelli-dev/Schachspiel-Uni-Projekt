package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ChessRulesTest
{
    @Test
    void rejectsMovesThatViolateBoardConstraints()
    {
        ChessRules rules = new ChessRules(new ChessBoard());

        assertAll(
                () -> assertFalse(
                        rules.checkRules(move("e3", "e4"), PieceColor.WHITE),
                        "source square must contain a piece"),
                () -> assertFalse(
                        rules.checkRules(move("a7", "a6"), PieceColor.WHITE),
                        "piece must belong to moving player"),
                () -> assertFalse(
                        rules.checkRules(move("e2", "e2"), PieceColor.WHITE),
                        "move must change position"),
                () -> assertFalse(
                        rules.checkRules(move("a1", "a2"), PieceColor.WHITE),
                        "own piece cannot be captured"));
    }

    @Test
    void slidingPieceCannotMoveThroughAnotherPiece()
    {
        ChessRules rules = new ChessRules(new ChessBoard());

        assertFalse(rules.checkRules(move("c1", "h6"), PieceColor.WHITE));
    }

    @Test
    void pawnCannotDoubleStepAfterLeavingInitialRank()
    {
        ChessRules rules = rulesFor("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1");

        assertFalse(rules.checkRules(move("e4", "e6"), PieceColor.WHITE));
    }

    @Test
    void pawnCaptureRulesDependOnDestinationOccupancy()
    {
        ChessRules emptyDiagonal = rulesFor("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1");
        ChessRules occupiedForward = rulesFor(
                "4k3/8/8/4p3/4P3/8/8/4K3 w - - 0 1");

        assertAll(
                () -> assertFalse(emptyDiagonal.checkRules(move("e4", "d5"),
                        PieceColor.WHITE)),
                () -> assertFalse(occupiedForward.checkRules(move("e4", "e5"),
                        PieceColor.WHITE)));
    }

    @Test
    void pawnMayCaptureAtEnPassantSquare()
    {
        ChessRules rules = rulesFor("4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 1");

        assertTrue(rules.checkRules(move("e5", "d6"), PieceColor.WHITE));
    }

    @Test
    void moveExposingOwnKingIsIllegalAndSimulationIsUndone()
    {
        ChessBoard board = new ChessBoard("k3r3/8/8/8/8/8/4R3/4K3 w - - 0 1");
        ChessRules rules = new ChessRules(board);

        assertFalse(rules.isMoveLegal(move("e2", "f2")));
        assertAll(() -> assertNotNull(board.getPieceAt(position("e2"))),
                () -> assertNull(board.getPieceAt(position("f2"))),
                () -> assertTrue(board.getMoveHistory()
                    .isEmpty()),
                () -> assertEquals(PieceColor.WHITE, board.getActivePlayer()));
    }

    @Test
    void detectsCheckAndCheckmateForActivePlayer()
    {
        ChessRules rules = rulesFor("7k/6Q1/6K1/8/8/8/8/8 b - - 0 1");

        assertAll(
                () -> assertEquals(GameState.BLACK_CHECK,
                        rules.checkForCheck()),
                () -> assertEquals(GameState.BLACK_CHECKMATE,
                        rules.checkForCheckmate()));
    }

    @Test
    void castlingRequiresClearPathAndMatchingRight()
    {
        ChessRules legal = rulesFor("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1");
        ChessRules noRight = rulesFor("4k3/8/8/8/8/8/8/R3K2R w - - 0 1");
        ChessRules blocked = rulesFor("4k3/8/8/8/8/8/8/R3KB1R w KQ - 0 1");

        assertAll(() -> assertTrue(legal.isMoveLegal(move("e1", "g1"))),
                () -> assertFalse(noRight.isMoveLegal(move("e1", "g1"))),
                () -> assertFalse(blocked.isMoveLegal(move("e1", "g1"))));
    }

    @Test
    void castlingThroughAttackedSquareIsIllegal()
    {
        ChessRules rules = rulesFor("4kr2/8/8/8/8/8/8/4K2R w K - 0 1");

        assertFalse(rules.isMoveLegal(move("e1", "g1")));
    }

    private static ChessRules rulesFor(String fen)
    {
        return new ChessRules(new ChessBoard(fen));
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
