package edu.wou.cs361.minesweeper.game;

import edu.wou.cs361.minesweeper.model.Piece;
import edu.wou.cs361.minesweeper.model.Space;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public abstract class GameBase implements IGame {

    /**
     * Random number generator used to create game seeds
     * Minefields use a separate random object created using a specific seed
     */
    protected Random RANDOM = new Random();

    // the number of flags currently on the board
    protected int flagCount;

    // resizable
    protected transient Queue<Space> boardUpdates;

    // board data
    protected Piece[][] board;
    protected boolean[][] mines;
    protected boolean gameOver;
    protected boolean victory;

    // determines if the first click happened yet
    protected boolean firstClick;

    // determines if we have used our cheats
    protected int cheatsAllowed;

    // board dimensions
    protected int width;
    protected int height;
    protected int mineCount;
    protected int cheats;

    protected GameBase(int width, int height, int mineCount, int cheats) {
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        this.cheats = cheats;

        this.board = new Piece[height][width];
        this.mines = new boolean[height][width];

        // sanity check in case the GUI fails to correct the number
        this.cheatsAllowed = this.cheats = cheats;

        this.boardUpdates = new LinkedList<>();
    }
}
