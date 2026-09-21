package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.ChessGame;
import ui.ChessBoardPanel;

/**
 * this class reacts to Action events
 */
public class ItemController implements ActionListener
{
    private ChessGame chessGame;
    private ChessBoardPanel chessBoardPanel;
    private JFrame windowFrame;

    /**
     * initializes ItemController
     * @param chessGame  the game logic
     * @param chessBoardPanel the board of the chess game
     * @param windowFrame the main window of the game
     */
    public ItemController(ChessGame chessGame, ChessBoardPanel chessBoardPanel,
            JFrame windowFrame)
    {
        this.chessGame = chessGame;
        this.chessBoardPanel = chessBoardPanel;
        this.windowFrame = windowFrame;

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (command.equals("Speichern"))
        {
            JFileChooser saveFileChooser = new JFileChooser();
            if (saveFileChooser
                .showSaveDialog(windowFrame) == JFileChooser.APPROVE_OPTION)
            {
                chessGame.saveBoard(saveFileChooser.getSelectedFile()
                    .getAbsolutePath());
            }

        }

        if (command.equals("Laden"))
        {
            JFileChooser loadFileChooser = new JFileChooser();
            if (loadFileChooser
                .showOpenDialog(windowFrame) == JFileChooser.APPROVE_OPTION)
            {
                boolean loaded = chessGame
                    .loadBoard(loadFileChooser.getSelectedFile()
                        .getAbsolutePath());
                if (loaded)
                {
                    chessBoardPanel.clearSelection();
                    chessBoardPanel.clearLegalPositions();
                    chessBoardPanel.clearPlayer();
                    chessBoardPanel.repaint();
                    JOptionPane.showMessageDialog(windowFrame,
                            "Das Spiel wurde erfolgreich geladen.");
                }

            }
        }

        if (command.equals("Brett zurücksetzen"))
        {
            chessGame.resetBoard();
            chessBoardPanel.clearSelection();
            chessBoardPanel.clearLegalPositions();
            chessBoardPanel.repaint();
            chessBoardPanel.clearPlayer();
        }

    }

}
