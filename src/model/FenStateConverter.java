package model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

/**
 * A utility class for converting between FEN (Forsyth–Edwards Notation) strings
 * and {@link FenState} objects.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation#Definition">FEN Wikipedia article</a>
 */
public class FenStateConverter
{

    private static final int PIECE_DATA = 0;
    private static final int ACTIVE_COLOR = 1;
    private static final int CASTLING = 2;
    private static final int EN_PASSANT = 3;
    private static final int HALFMOVE = 4;
    private static final int FULLMOVE = 5;
    
    /**
     * Creates an instance of a FenStateConverter class.
     * 
     * Note: This class only has static methods, so instantiating it is not necessary.
     */
    public FenStateConverter() { }

    /**
     * Converts a single character representing a chess piece into a ChessPiece object at the given position.
     *
     * @param pieceChar The character representing the piece (e.g., 'P', 'p').
     * @param position The position of the piece.
     * @return A new ChessPiece instance, or null if the character does not represent a valid piece.
     *
     * @require position != null
     * @see ChessPiece
     * @see Position
     */
    private static ChessPiece pieceDataToChessPiece(char pieceChar,
            Position position)
    {
        assert position != null : "position should not be null";

        boolean isUpper = Character.isUpperCase(pieceChar);
        PieceColor color;
        if (isUpper)
            color = PieceColor.WHITE;
        else
            color = PieceColor.BLACK;

        PieceType type = switch (Character.toLowerCase(pieceChar))
        {
        case 'p':
            yield PieceType.PAWN;
        case 'n':
            yield PieceType.KNIGHT;
        case 'b':
            yield PieceType.BISHOP;
        case 'r':
            yield PieceType.ROOK;
        case 'q':
            yield PieceType.QUEEN;
        case 'k':
            yield PieceType.KING;
        default:
            yield null;
        };

        if (type == null) return null;

        return new ChessPieceFactory(type, color).withPosition(position)
            .build();
    }

    /**
     * Converts a ChessPiece to its corresponding FEN character.
     * 
     * @param p The piece to convert.
     * @return The FEN character representation of the piece.
     * 
     * @require p != null
     */
    private static char chessPieceToPieceChar(ChessPiece p)
    {
        assert p != null : "p should not be null";
        char pieceChar = switch (p.getPieceType())
        {
        case PAWN:
            yield 'p';
        case KNIGHT:
            yield 'n';
        case BISHOP:
            yield 'b';
        case ROOK:
            yield 'r';
        case QUEEN:
            yield 'q';
        case KING:
            yield 'k';
        };

        if (p.getPieceColor() == PieceColor.WHITE)
            return Character.toUpperCase(pieceChar);
        else
            return pieceChar;
    }

    /**
     * Converts a castling character to its corresponding CastlingRights enum.
     * 
     * @param c The character to convert.
     * @return The CastlingRights enum, or null if the character is invalid.
     */
    private static CastlingRights castlingFromChar(char c)
    {
        return switch (c)
        {
        case 'K' -> CastlingRights.WHITE_KING_SIDE;
        case 'Q' -> CastlingRights.WHITE_QUEEN_SIDE;
        case 'k' -> CastlingRights.BLACK_KING_SIDE;
        case 'q' -> CastlingRights.BLACK_QUEEN_SIDE;
        default -> null;
        };
    }

    /**
     * Converts a set of castling availability to its FEN string representation.
     * 
     * @param castlingAvailability The set of available castling rights.
     * @return The FEN string representation.
     * 
     * @require castlingAvailability != null
     */
    private static String castlingToString(
            Set<CastlingRights> castlingAvailability)
    {
        assert castlingAvailability != null : "castlingAvailability should not be null";
        if (castlingAvailability == null || castlingAvailability.isEmpty())
        {
            return "-";
        }

        String rightsString = "";

        if (castlingAvailability.contains(CastlingRights.WHITE_KING_SIDE))
        {
            rightsString += "K";
        }
        if (castlingAvailability.contains(CastlingRights.WHITE_QUEEN_SIDE))
        {
            rightsString += "Q";
        }
        if (castlingAvailability.contains(CastlingRights.BLACK_KING_SIDE))
        {
            rightsString += "k";
        }

        if (castlingAvailability.contains(CastlingRights.BLACK_QUEEN_SIDE))
        {
            rightsString += "q";
        }

        return rightsString;
    }

    /**
     * Parses a FEN string into a FenState object.
     *
     * @param fen The FEN notation string.
     * @return A FenState representing the parsed board state.
     * @throws DataFormatException If the FEN string is malformed or contains invalid data.
     *
     * @require fen != null
     * @see FenState
     */
    public static FenState fromString(String fen) throws DataFormatException
    {
        assert fen != null : "fen should not be null";

        String[] fenFields = fen.strip()
            .split(" ");
        if (fenFields.length != 6)
        {
            throw new DataFormatException(
                    "FEN notation does not contain all expected elements.");
        }

        List<ChessPiece> activePieces = new ArrayList<>();

        String pieceData = fenFields[PIECE_DATA];
        String[] piecesByRank = pieceData.split("/");

        if (piecesByRank.length != 8)
        {
            throw new DataFormatException(
                    "Piece data must contain exactly 8 ranks.");
        }

        for (int i = 0; i < 8; i++)
        {
            int currentRank = 8 - i;
            int currentFile = 0;
            char[] pieces = piecesByRank[i].toCharArray();
            for (char p : pieces)
            {
                if (Character.isDigit(p))
                {
                    currentFile += Character.getNumericValue(p);
                    if (currentFile > 8)
                    {
                        throw new DataFormatException(
                                "File index out of bounds on rank "
                                        + currentRank);
                    }
                    continue;
                }

                currentFile++; // increment now to not conflict with empty field logic

                if (currentFile > 8)
                {
                    throw new DataFormatException(
                            "File index out of bounds on rank " + currentRank);
                }

                Position position = new Position(currentFile, currentRank);
                ChessPiece piece = pieceDataToChessPiece(p, position);

                if (piece != null) activePieces.add(piece);
            }
        }

        String activeColorString = fenFields[ACTIVE_COLOR];
        PieceColor activeColor;
        if (activeColorString.equals("w"))
            activeColor = PieceColor.WHITE;
        else if (activeColorString.equals("b"))
            activeColor = PieceColor.BLACK;
        else
            throw new DataFormatException(
                    "Failed to determine active color on position 2 of the string.");

        String castlingAvailabilityString = fenFields[CASTLING];
        Set<CastlingRights> castlingAvailability = EnumSet
            .noneOf(CastlingRights.class);

        if (!castlingAvailabilityString.equals("-"))
        {
            for (char c : castlingAvailabilityString.toCharArray())
            {
                CastlingRights right = castlingFromChar(c);
                if (right == null)
                {
                    throw new DataFormatException(
                            "Invalid castling symbol: " + c);
                }
                castlingAvailability.add(right);
            }
        }

        String enPassantSquareString = fenFields[EN_PASSANT];
        Position enPassantSquare = null;
        if (!enPassantSquareString.equals("-"))
        {
            int rank = Character
                .getNumericValue(enPassantSquareString.charAt(1));
            if (rank != 3 && rank != 6)
            {
                throw new DataFormatException(
                        "En‑passant square must be on rank 3 or 6.");
            }
            int file = enPassantSquareString.charAt(0) - 'a' + 1;
            enPassantSquare = new Position(file, rank);
        }

        int halfmoveClock = 0;

        try
        {
            halfmoveClock = Integer.parseInt(fenFields[HALFMOVE]);
            if (halfmoveClock < 0)
            {
                throw new DataFormatException(
                        "Halfmove clock has to be non-negative.");
            }
        }
        catch (NumberFormatException e)
        {
            throw new DataFormatException(
                    "Unexpected value for halfmove clock: "
                            + fenFields[HALFMOVE]);
        }

        int fullmoveNumber = 1;

        try
        {
            fullmoveNumber = Integer.parseInt(fenFields[FULLMOVE]);
            if (fullmoveNumber < 1)
            {
                throw new DataFormatException(
                        "Fullmove number has to be greater than 0.");
            }
        }
        catch (NumberFormatException e)
        {
            throw new DataFormatException(
                    "Unexpected value for fullmove number: "
                            + fenFields[FULLMOVE]);
        }

        FenState fenState = new FenState(activePieces, activeColor,
                castlingAvailability, enPassantSquare, halfmoveClock,
                fullmoveNumber);
        return fenState;
    }

    /**
     * Converts a FenState object into its FEN string representation.
     *
     * @param fenState The state to convert.
     * @return A FEN-compliant string representing the given state.
     *
     * @require fenState != null
     * @see FenState
     */
    public static String toString(FenState fenState)
    {
        assert fenState != null : "fenState should not be null";

        String fenString = "";

        List<ChessPiece> activePieces = new ArrayList<ChessPiece>(
                fenState.getActivePieces());

        activePieces.sort(new ChessPieceComparator());

        String pieceData = "";
        int pieceIndex = 0;

        for (int currentRank = 8; currentRank >= 1; currentRank--)
        {
            String rankString = "";
            int emptyFields = 0;

            for (int currentFile = 1; currentFile <= 8; currentFile++)
            {
                ChessPiece piece = pieceIndex < activePieces.size()
                        ? activePieces.get(pieceIndex) : null;
                Position position = piece == null ? null : piece.getPosition();

                if (position != null && position.getY() == currentRank
                        && position.getX() == currentFile)
                {
                    if (emptyFields > 0) rankString += emptyFields;
                    rankString += chessPieceToPieceChar(piece);
                    emptyFields = 0;
                    pieceIndex++;
                }
                else
                    emptyFields++;
            }

            if (emptyFields > 0) rankString += emptyFields;

            pieceData += rankString;
            if (currentRank > 1) pieceData += "/";
        }

        fenString += pieceData;
        fenString += " ";

        PieceColor activeColor = fenState.getActiveColor();

        fenString += activeColor == PieceColor.WHITE ? "w" : "b";
        fenString += " ";

        Set<CastlingRights> castlingAvailability = fenState
            .getCastlingAvailability();

        fenString += castlingToString(castlingAvailability);
        fenString += " ";

        Position enPassantSquare = fenState.getEnPassantSquare();

        if (enPassantSquare == null)
        {
            fenString += "-";
            fenString += " ";
        }
        else
        {
            int file = enPassantSquare.getX();
            char fileChar = (char) ('a' + (file - 1));
            int rank = enPassantSquare.getY();

            fenString += fileChar;
            fenString += rank;
            fenString += " ";
        }

        fenString += fenState.getHalfmoveClock();
        fenString += " ";

        fenString += fenState.getFullmoveNumber();

        return fenString;
    }
}
