package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import controller.ItemController;
import model.ChessGame;

/**
 * The main window which contains the board,a menu and a player display
 */
public class ChessWindow
{
    private final JFrame windowFrame;
    private JPanel buttonPanel;
    private PlayerIndicator whiteIndicator;
    private PlayerIndicator blackIndicator;
    private ChessGame chessGame;
    private ChessBoardPanel chessBoardPanel;
    private ItemController itemController;

    public ChessWindow()
    {
        chessGame = new ChessGame();
        windowFrame = new JFrame("Chess Game");
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        windowFrame.setResizable(false); //the size cannot be changed
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        whiteIndicator = new PlayerIndicator();
        blackIndicator = new PlayerIndicator();
        createPlayerDisplay();

        windowFrame.add(buttonPanel, BorderLayout.SOUTH);
        chessBoardPanel = new ChessBoardPanel(chessGame, whiteIndicator,
                blackIndicator);

        windowFrame.add(chessBoardPanel);
        itemController = new ItemController(chessGame, chessBoardPanel,
                windowFrame);
        createMenu();

        windowFrame.pack();
        windowFrame.setLocationRelativeTo(null);
        windowFrame.setVisible(true);

    }

    /**
     * Creates a player display
     */
    public void createPlayerDisplay()
    {
        whiteIndicator.setColor(Color.WHITE);
        blackIndicator.setColor(Color.LIGHT_GRAY);
        buttonPanel.add(whiteIndicator);
        buttonPanel.add(blackIndicator);

    }

    /**
     * Creates a menu bar which contains a menu and the options save, load and reset
     */
    public void createMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Datei");
        JMenuItem saveItem = new JMenuItem("Speichern");
        JMenuItem loadItem = new JMenuItem("Laden");
        JMenuItem resetItem = new JMenuItem("Brett zurücksetzen");

        saveItem.addActionListener(itemController);

        loadItem.addActionListener(itemController);
        resetItem.addActionListener(itemController);

        menuBar.add(menu);
        menu.add(saveItem);
        menu.add(loadItem);
        menu.add(resetItem);
        windowFrame.setJMenuBar(menuBar);

    }

}
