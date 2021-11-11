package edu.wou.cs361.minesweeper.game;

import edu.wou.cs361.minesweeper.model.Piece;
import edu.wou.cs361.minesweeper.model.Space;

import java.io.*;
import java.util.Queue;

public interface IGame extends Serializable {
    /**
     * Load the board from a binary file
     *
     * @param filename Location of the game
     * @return The board, or null if invalid
     */
    static IGame loadGame(String filename) {
        try {
            var file = new FileInputStream(filename + ".bin");
            var stream = new ObjectInputStream(file);
            var board = (IGame) stream.readObject();
            stream.close();
            return board;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new game with the same width and height dimensions,
     * resets the game data, and update the spaces
     */
    void newGame();

    /**
     * Saves the board to a binary file
     *
     * @param filename Location of the game
     */
    default void saveGame(String filename) {
        try {
            var file = new FileOutputStream(filename + ".bin");
            var stream = new ObjectOutputStream(file);
            stream.writeObject(this);
            stream.flush();
            stream.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Restarts the current game
     * If we haven't clicked yet, do nothing.
     * Reset the game data and update the spaces
     */
    void restart();

    /**
     * Handle standard game clicks
     * if game is over, do nothing. Otherwise, handle all possible ClickActions
     *
     * @param space  Space clicked
     * @param action Type of click
     */
    void handleClick(Space space, ClickAction action);

    /**
     * Gets the piece on the specified space
     * If the space is invalid, return null, otherwise, return the piece
     * on the specified space
     *
     * @param space Space to check for a piece
     * @return the piece on the specified space
     */
    Piece getPiece(Space space);

    /**
     * Determines if a space appears enabled, or pressed up
     * If a space is invalid, return false, otherwise, get the piece
     * If the piece is null, that means the space is empty and pops up, so return true
     * Default, just return piece.isEnabled() value
     *
     * @param space Space to check
     * @return True if the space appears enabled
     */
    boolean isEnabled(Space space);

    /**
     * Gets the number of mines left on the board. This should equal
     * the mineCount - flagCount
     *
     * @return number of mines remaining
     */
    int getRemainingMines();

    /**
     * Gets the width of the board
     *
     * @return The width of the board
     */
    int getWidth();

    /**
     * Gets the height of the board
     *
     * @return The height of the board
     */
    int getHeight();

    /**
     * Checks if the player has used their cheat
     *
     * @return if the player has cheats left (cheats > 0)
     */
    boolean canCheat();

    /**
     * Checks if the game ended
     *
     * @return true if the game ended (gameOver)
     */
    boolean gameOver();

    /**
     * Checks how many cheats the player has left
     *
     * @return remaining cheats
     */
    int getCheats();

    /**
     * Checks if the player won the game
     *
     * @return true if the player won the game (victory)
     */
    boolean hasWon();

    /**
     * Checks if a space is within the bounds of this board. This entails:
     * If the space is null, x < 0 OR y < 0, x >= height OR y < 0, then it's not valid
     * Otherwise, return true
     *
     * @param space Space to check
     * @return true if the space is within this board
     */
    boolean isValid(Space space);

    /**
     * Gets a list of spaces pending updates to update the button displays
     *
     * @return a list of spaces needing an update (boardUpdates)
     */
    Queue<Space> getUpdates();

    /**
     * Actions that can be performed on click.
     * Used by buttons to pass along a constant rather than a list of modifiers.
     */
    enum ClickAction {
        /**
         * Normal click
         */
        DEFAULT,
        /**
         * Placing a flag or mark
         */
        FLAG,
        /**
         * Placing a mark of different colors
         */
        MARK,
        /**
         * Safely clicking a mine
         */
        CHEAT
    }
}
