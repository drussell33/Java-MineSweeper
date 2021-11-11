package edu.wou.cs361.minesweeper.ui;

import edu.wou.cs361.minesweeper.game.IGame;
import edu.wou.cs361.minesweeper.model.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Custom buttons used for the main gameplay spaces, to allow accessing both
 * the board and location from the action listener
 */
public class MineButton extends JButton implements MouseListener {
    // determines if the mouse is over this button
    boolean inArea;
    private MainUI gui;
    private IGame board;
    private Space space;

    /**
     * Creates a new button
     *
     * @param gui   GUI object containing the button
     * @param board Board object containing the space
     * @param space Location of the button on the board
     */
    public MineButton(MainUI gui, IGame board, Space space) {
        // data storage
        this.gui = gui;
        this.board = board;
        this.space = space;

        // fix odd spacing around the image
        this.setMargin(new Insets(0, 0, 0, 0));

        // mouse listener so we can track right clicks and click the numbers
        this.addMouseListener(this);

        // stop the button from having the ugly selected boarder
        this.setFocusable(false);

        // when loading a edu.wou.cs361.minesweeper.game, display it immeditelly
        this.updatePiece();
    }

    /**
     * Draws the button with its icon
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // safety
        if (board == null) {
            return;
        }

        // determine what we are drawing
        var piece = board.getPiece(space);
        if (piece != null && piece.getIcon() != null) {
            // calculate the position of the icon

            // dimensions should be square
            var size = Math.min(getWidth(), getHeight()) / 2;

            // and centered
            g.drawImage(piece.getIcon(),
                    (getWidth() - size) / 2,
                    (getHeight() - size) / 2,
                    size,
                    size,
                    null, null);
        }
    }

    /**
     * Returns the space this button represents
     *
     * @return The space this button represents
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Updates piece
     */
    public void updatePiece() {
        if (board == null) {
            return;
        }

        setEnabled(board.isEnabled(space));

        repaint();
    }


    /**
     * Called when the mouse enters the area
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        inArea = true;
    }

    /**
     * Called when the mouse exits the area
     */
    @Override
    public void mouseExited(MouseEvent e) {
        inArea = false;
    }

    /**
     * Called when the mouse is released
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // needs to still be in the component to click
        if (!inArea) {
            return;
        }

        // first, determine the click type
        IGame.ClickAction action = null;

        // shift click: toggle mark
        // left click: normal click
        if (SwingUtilities.isLeftMouseButton(e)) {
            // cheat button was pressed, so cheat click
            if (gui.isCheating()) {
                action = IGame.ClickAction.CHEAT;
            }
            // otherwise default click
            else {
                action = IGame.ClickAction.DEFAULT;
            }
        }
        // right clicks for flags
        else if (SwingUtilities.isRightMouseButton(e)) {
            // shift toggles the mark color
            if (e.isShiftDown()) {
                action = IGame.ClickAction.MARK;
            }
            // not shift does flags
            else {
                action = IGame.ClickAction.FLAG;
            }
        }


        // if we have an action (no middle click), click it
        if (action != null) {
            board.handleClick(this.getSpace(), action);

            // update spaces on the board
            gui.updateSpaces();

            // if we are out of cheats, mark that
            if (action == IGame.ClickAction.CHEAT) {
                gui.updateCheat();
            }
        }

        // set the win status if relevant
        if (board.gameOver()) {
            gui.gameOver(board.hasWon());
        }
    }

    // Required by the interface, but unused
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

}