package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * ChessGame provides all required functionality to play a game of chess.
 * It handles game and board state as well as move validation and legal position preview.
 */
public class ChessGame
{
    private ChessBoard board;
    private ChessRules rules;
    private MoveProcessor moves;

    /**
     * Initializes the fields of the chess game.
     * 
     * @param fenString The FEN string to initialize the board from, or null for default.
     */
    private void initializeFields(String fenString)
    {
        if (fenString == null)
            board = new ChessBoard();
        else
            board = new ChessBoard(fenString);

        if (board.getState() == GameState.IMPORT_FAILED)
        {
            System.out.println(board.getImportError());
            System.exit(1);
        }

        rules = new ChessRules(board);
        moves = new MoveProcessor(board, rules);
    }

    /**
     * Initializes the chess game with a default starting board state.
     */
    public ChessGame()
    {
        initializeFields(null);
    }

    /**
     * Loads a chess board state from a file containing a FEN string.
     * 
     * @param filePath The path to the file containing the FEN string.
     * @return true if the board was successfully loaded, false otherwise.
     * 
     * @require filePath != null
     */
    public boolean loadBoard(String filePath)
    {
        assert filePath != null : "filePath should not be null";
        File f = new File(filePath);
        if (!f.exists() && !f.isFile() && f.canRead()) return false;

        Scanner scanner = null;
        String fenString = null;

        try
        {
            scanner = new Scanner(f);
            if (scanner.hasNextLine()) fenString = scanner.nextLine();
        }

        catch (FileNotFoundException e)
        {
            return false;
        }

        finally
        {
            if (scanner != null) scanner.close();
        }

        if (fenString == null) return false;

        initializeFields(fenString);
        return true;
    }

    /**
     * Saves the current chess board state to a file as a FEN string.
     * 
     * @param filePath The path where the FEN string will be saved.
     * 
     * @require filePath != null
     */
    public void saveBoard(String filePath)
    {
        assert filePath != null : "filePath should not be null";
        Path path = Paths.get(filePath);
        try
        {
            Files.write(path, Arrays.asList(board.exportStateToFenString()),
                    StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            // TODO: should tell the user -> return success status (boolean)?
            System.out.println("Could not write FEN export to file " + filePath
                    + "\n" + e.getMessage());
        }
    }

    /**
     * Resets the game to its initial starting state.
     */
    public void resetBoard()
    {
        initializeFields(null);
    }

    /**
     * Returns an unmodifiable list of the currently active pieces on the board.
     * 
     * @return Unmodifiable list of active pieces.
     * @see ChessPiece
     */
    public List<ChessPiece> getActivePieces()
    {
        return board.getActivePieces();
    }

    /**
     * Returns the color of the currently active player.
     * 
     * @return Color of the active player.
     * @see PieceColor
     */
    public PieceColor getActivePlayer()
    {
        return board.getActivePlayer();
    }

    /**
     * Returns the current state of the game.
     * 
     * @return The current game state.
     * @see GameState
     */
    public GameState getGameState()
    {
        return board.getState();
    }

    /**
     * Returns a list of legal positions that the given piece can move to.
     * 
     * @param piece The chess piece for which to find legal moves.
     * @return A list of valid destination positions.
     * 
     * @require piece != null
     * @see ChessPiece
     * @see Position
     */
    public List<Position> getLegalPositionsForPiece(ChessPiece piece)
    {
        assert piece != null : "piece should not be null";
        List<Move> legalMoves = rules.generateLegalMovesFor(piece);
        // ChessBoardPanel.clearLegalPositions requires modifiable list -> .collect()
        List<Position> legalPositions = legalMoves.stream()
            .map(Move::getNewPosition)
            .collect(Collectors.toList());
        return legalPositions;
    }

    /**
     * Attempts to apply a move to the game. The move is only applied if it is legal.
     * 
     * @param move The move to attempt to apply.
     * @return true if the move was valid and applied, false otherwise.
     * 
     * @require move != null
     * @see Move
     */
    public boolean applyMove(Move move)
    {
        assert move != null : "move should not be null";
        if (!rules.isMoveLegal(move)) return false;
        moves.applyValidMove(move);
        return true;
    }
}
