package edu.wou.cs361.minesweeper.ui;

import edu.wou.cs361.minesweeper.Startup;
import edu.wou.cs361.minesweeper.game.IGame;
import edu.wou.cs361.minesweeper.model.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Main GUI object for Minesweeper game. Called game play is ready
 */
public class MainUI extends JFrame implements ActionListener {
    // data
    private IGame game;

    // states
    private boolean isCheating;

    // buttons and labels
    private JLabel mines;
    private MineButton[][] buttons;

    private JButton buttonNewGame;
    private JButton buttonCheat;

    // menus
    private JMenuBar bar;
    private JMenu menuMinesweeper;

    private JMenu menuNewGame;
    private JMenuItem buttonRestart;
    private JMenuItem buttonSave;
    private JMenuItem buttonLoad;
    private JMenuItem buttonExit;

    private JMenu menuHelp;
    private JMenuItem buttonRules;
    private JMenuItem buttonMenu;
    private JMenuItem buttonDifficulty;

    /**
     * Makes a new minesweeper window
     *
     * @param game Input board, the display will take the size and pieces on the board
     */
    public MainUI(IGame game) {
        this.setTitle("Minesweeper");
        this.game = game;

        // construct the top menu
        bar = new JMenuBar();
        setJMenuBar(bar);

        // miscellaneous actions
        menuMinesweeper = new JMenu("Minesweeper");
        bar.add(menuMinesweeper);

        // new edu.wou.cs361.minesweeper.game menu
        menuNewGame = new JMenu("New Game");
        menuMinesweeper.add(menuNewGame);

        // add all difficulties
        for (var difficulty : Startup.Difficulty.values()) {
            DifficultyButton button = new DifficultyButton(difficulty);
            menuNewGame.add(button);
        }

        // restarts the current edu.wou.cs361.minesweeper.game
        buttonRestart = new JMenuItem("Restart");
        buttonRestart.addActionListener(this);
        menuMinesweeper.add(buttonRestart);

        // saves the current edu.wou.cs361.minesweeper.game
        buttonSave = new JMenuItem("Save Game");
        buttonSave.addActionListener(this);
        menuMinesweeper.add(buttonSave);

        // loads a new edu.wou.cs361.minesweeper.game from a file
        buttonLoad = new JMenuItem("Load Game");
        buttonLoad.addActionListener(this);
        menuMinesweeper.add(buttonLoad);

        // exits the edu.wou.cs361.minesweeper.game
        menuMinesweeper.addSeparator();
        buttonExit = new JMenuItem("Exit");
        buttonExit.addActionListener(this);
        menuMinesweeper.add(buttonExit);

        // help menu
        menuHelp = new JMenu("Help");
        bar.add(menuHelp);

        // shows the rules
        buttonRules = new JMenuItem("Rules");
        buttonRules.addActionListener(this);
        menuHelp.add(buttonRules);

        // shows help about the menu
        buttonMenu = new JMenuItem("Menus");
        buttonMenu.addActionListener(this);
        menuHelp.add(buttonMenu);

        // shows available difficulties
        buttonDifficulty = new JMenuItem("Difficulties");
        buttonDifficulty.addActionListener(this);
        menuHelp.add(buttonDifficulty);


        // GUI Layout

        // gives use four regions and a center
        var pane = getContentPane();
        pane.setLayout(new BorderLayout());


        // new game with same dimensions
        buttonNewGame = new JButton("New Game");
        buttonNewGame.addActionListener(this);

        // remaining mines display
        mines = new JLabel("Mines", SwingConstants.CENTER);
        updateMines();

        // top row of the board
        var top = new Container();
        top.setLayout(new GridLayout(1, 3));
        top.add(mines);
        top.add(buttonNewGame);

        // cheating, skip if 0 cheats to start
        if (game.canCheat()) {
            buttonCheat = new JButton();
            updateCheat();
            buttonCheat.addActionListener(this);
            top.add(buttonCheat);
        } else {
            top.add(new JLabel(""));
        }

        top.setPreferredSize(new Dimension(20, top.getFontMetrics(buttonNewGame.getFont()).getHeight() * 2));

        pane.add(top, BorderLayout.NORTH);

        // main button grid
        var minesContainer = new Container();
        int width2 = game.getWidth();
        int height2 = game.getHeight();
        minesContainer.setLayout(new GridLayout(height2, width2));
        buttons = new MineButton[height2][width2];

        // and add them all
        for (var y = 0; y < height2; y++) {
            for (var x = 0; x < width2; x++) {
                // add buttons, and store them
                buttons[y][x] = new MineButton(this, game, new Space(x, y));
                minesContainer.add(buttons[y][x]);
            }
        }

        pane.add(minesContainer, BorderLayout.CENTER);

        // sizes, we need them for the top bar
        var width = game.getWidth() * 40;
        var height = game.getHeight() * 40 + 100;

        setSize(width, height);
        setVisible(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Gets a .MineButton at the specified location
     *
     * @param space Location of the button
     * @return the button at the location
     */
    public MineButton getButton(Space space) {
        if (!game.isValid(space)) {
            return null;
        }
        return buttons[space.getY()][space.getX()];
    }

    /**
     * Updates all spaces pending update to display their current value
     */
    public void updateSpaces() {
        var queue = game.getUpdates();
        while (!queue.isEmpty()) {
            var space = queue.poll();
            var update = getButton(space);
            if (update != null) {
                update.updatePiece();
            }
        }

        updateMines();
    }

    /**
     * Updates the mines display
     */
    public void updateMines() {
        mines.setText(String.format("Mines: %d", game.getRemainingMines()));
    }

    /**
     * Sets the cheat button to used
     */
    public void updateCheat() {
        // if we can cheat, set the label
        if (game.canCheat()) {
            buttonCheat.setText("Cheats: " + game.getCheats());
            buttonCheat.setEnabled(true);
        }

        isCheating = false;
    }

    /**
     * Sets the cheat button to used
     */
    public void resetButtons() {
        buttonNewGame.setText("New Game");

        updateCheat();
        updateSpaces();
    }

    /**
     * Sets the winning text
     *
     * @param victory Whether the player won
     */
    public void gameOver(boolean victory) {
        if (victory) {
            buttonNewGame.setText("You Win!");
        } else {
            buttonNewGame.setText("You Lose");
        }
    }

    /**
     * Checks if the cheat button is active
     *
     * @return true if the cheat button is active
     */
    public boolean isCheating() {
        return isCheating;
    }

    /**
     * Sets the cheat status
     *
     * @param cheat Whether the user is in cheat mode
     */
    private void setCheating(boolean cheat) {
        // and change the button text
        if (cheat) {
            buttonCheat.setText("Cancel cheat");
            isCheating = true;
        }
        // otherwise, run general cheating code
        else {
            updateCheat();
        }
    }

    /**
     * Called when the 'X' button is pressed on the window
     *
     * @param e Event the button is called with
     */
    public void windowClosing(WindowEvent e) {
        // simply run the menu
        //this.menu();
    }

    /**
     * Handles all single instance buttons
     *
     * @param e Calling event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        var button = e.getSource();

        // restart the current edu.wou.cs361.minesweeper.game
        if (button == buttonNewGame) {
            // make the board new
            game.newGame();

            // and clear edu.wou.cs361.minesweeper.game data
            resetButtons();
            return;
        }

        // restart the current edu.wou.cs361.minesweeper.game
        if (button == buttonRestart) {
            // make the board new
            game.restart();

            // and clear edu.wou.cs361.minesweeper.game data
            resetButtons();
            return;
        }

        // saves the game
        if (button == buttonSave) {
            var filename = JOptionPane.showInputDialog(this,
                    "Enter filename to save game",
                    "MineSweeper - Save game",
                    JOptionPane.QUESTION_MESSAGE
            );

            // user canceled
            if (filename == null || filename.equals("")) {
                return;
            }

            // if it exists, prompt to continue
            if (new File(filename + ".bin").exists()) {
                var result = JOptionPane.showConfirmDialog(this,
                        "Save game " + filename + " already exists, overwrite?",
                        "MineSweeper - Save game",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            game.saveGame(filename);
        }

        // loads the edu.wou.cs361.minesweeper.game
        if (button == buttonLoad) {
            var filename = JOptionPane.showInputDialog(this,
                    "Enter filename to load game",
                    "MineSweeper - Load game",
                    JOptionPane.QUESTION_MESSAGE
            );

            // user canceled
            if (filename == null || filename.equals("")) {
                return;
            }

            // if it exists, prompt to continue
            if (new File(filename + ".bin").exists()) {
                var board = (IGame) IGame.loadGame(filename);
                if (board != null) {
                    Startup.startGame(board);
                }
                // did not load right
                else {
                    JOptionPane.showMessageDialog(this,
                            "Error reading save game " + filename,
                            "MineSweeper - Load game",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // file does not exist
                JOptionPane.showMessageDialog(this,
                        "Saved game " + filename + " does not exist",
                        "MineSweeper - Load game",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        // cheat button
        if (button == buttonCheat) {
            // no cheating too many times
            if (!game.canCheat()) {
                return;
            }

            // toggle cheating
            setCheating(!isCheating);
            return;
        }

        // cheat button
        if (button == buttonExit) {
            this.dispose();
            return;
        }


        /* edu.wou.cs361.minesweeper.ui.Help */

        // rules
        if (button == buttonRules) {
            Help.showRules(this);
        }

        // menus
        if (button == buttonMenu) {
            Help.menuHelp(this);
        }

        // rules
        if (button == buttonDifficulty) {
            Help.difficultyHelp(this);
        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path        Icon path
     * @param description Basic description of the icon
     * @return Returns the icon at the path, or null if the path is invalid
     * @author Larry Vail
     */
    protected ImageIcon createImageIcon(String path, String description) {
        var imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Common code for difficulty buttons
     */
    private static class DifficultyButton extends JMenuItem implements ActionListener {
        // data
        private Startup.Difficulty difficulty;

        public DifficultyButton(Startup.Difficulty difficulty) {
            super(difficulty.getLabel());

            this.difficulty = difficulty;
            this.addActionListener(this);
        }

        /**
         * Called when the button is clicked
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Startup.newGame(difficulty);
        }
    }
}