package edu.wou.cs361.minesweeper.ui;

import edu.wou.cs361.minesweeper.game.Game;
import edu.wou.cs361.minesweeper.game.IGame;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.function.IntSupplier;

/**
 * Core panel for the custom game menu
 * <br>
 * Generally only one instance exists
 */
public class CustomMenu extends JPanel implements ChangeListener, KeyListener {
    // data
    private JSlider width, height;
    private JFormattedTextField fieldMines, fieldCheats;
    private IntSupplier maxMines, maxCheats;

    /**
     * Default constructor
     */
    public CustomMenu() {
        // start with an empty spot
        setLayout(new GridLayout(6, 1));

        // next, create the sliders
        width = new Slider();
        height = new Slider();

        // then add them with the labels
        add(new JLabel("Width", JLabel.CENTER));
        width.addChangeListener(this);
        add(width);
        add(new JLabel("Height", JLabel.CENTER));
        height.addChangeListener(this);
        add(height);

        var c = new Container();
        c.setLayout(new GridLayout(2, 2));
        c.add(new JLabel("Mines"));
        c.add(new JLabel("Cheats"));

        maxMines = () -> width.getValue() * height.getValue() - 9;
        var format = new Formatter(maxMines);
        fieldMines = new JFormattedTextField(format);
        fieldMines.addKeyListener(this);
        fieldMines.setValue(10);
        c.add(fieldMines);

        maxCheats = () -> (int) Math.sqrt(width.getValue() * height.getValue());
        format = new Formatter(maxCheats);
        fieldCheats = new JFormattedTextField(format);
        fieldMines.addKeyListener(this);
        fieldCheats.setValue(1);
        c.add(fieldCheats);
        add(c);
    }

    /**
     * Gets the color result
     *
     * @return the color result
     */
    public IGame createBoard() {
        var mines = Integer.parseInt(fieldMines.getText());
        var cheats = Integer.parseInt(fieldCheats.getText());
        return new Game(width.getValue(), height.getValue(), mines, cheats);
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        if (((JSlider) event.getSource()).getValueIsAdjusting()) {
            return;
        }
        var max = maxMines.getAsInt();
        if ((Integer) fieldMines.getValue() > max) {
            fieldMines.setValue(max);
        }
        max = maxCheats.getAsInt();
        if ((Integer) fieldCheats.getValue() > max) {
            fieldCheats.setValue(max);
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // only numeric allowed
        var c = event.getKeyChar();
        if (c < '0' || c > '9') {
            event.consume();
        }
    }

    /**
     * Sets data used by both sliders
     */
    private class Slider extends JSlider {
        /**
         * General constructor
         */
        public Slider() {
            super(JSlider.HORIZONTAL, 5, 50, 10);

            this.setMajorTickSpacing(5);
            this.setMinorTickSpacing(1);
            this.setPaintTicks(true);
            this.setPaintLabels(true);
        }
    }

    private class Formatter extends AbstractFormatter {
        private IntSupplier max;

        public Formatter(IntSupplier max) {
            this.max = max;
        }

        @Override
        public Object stringToValue(String str) throws ParseException {
            // never need more than 4 characters
            var max = this.max.getAsInt();
            if (str.matches("[0-9]{5,}")) {
                return max;
            }
            // parse the value
            return Math.min(Integer.parseInt(str), max);
        }

        @Override
        public String valueToString(Object i) throws ParseException {
            if (i == null) {
                return "";
            }
            return i.toString();
        }
    }
}