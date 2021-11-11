package edu.wou.cs361.minesweeper.game;

import edu.wou.cs361.minesweeper.model.Piece;
import edu.wou.cs361.minesweeper.model.Space;

import java.util.*;

/**
 * Contains all the visible pieces in the Minesweeper Game
 */
public class Game extends GameBase {
    /**
     * Creates a new Minesweeper board with the specified dimensions
     *
     * @param width     Width of the board
     * @param height    Height of the board
     * @param mineCount Total mines
     * @param cheats    Cheats allowed
     */
    public Game(int width, int height, int mineCount, int cheats) {

        super(width, height, mineCount, cheats);
        newGame();
    }

    /**
     * Creates a new game with the same width and height dimensions,
     * resets the game data, and update the spaces
     */
    @Override
    public void newGame() {
        //1.reset the data
        restoreDefaults();
        //2.rest the mines
        this.mines = new boolean[height][width];

        //3.make the spaces as all uncovered
        for(Space space : everySpace()){
            markUpdate(space);
        }
    }

    /**
     * Restarts the current game
     * If we haven't clicked yet, do nothing.
     * Reset the game data and update the spaces
     */
    @Override
    public void restart() {
        restoreDefaults();

        for(Space space : everySpace()){
            markUpdate(space);
        }
    }

    private Space[] everySpace() {
        Space[] everySpace = new Space[height * width];
        var currentIndex = 0;
        for(var i = 0; i < height; ++i){
            for(var j = 0; j < width; ++j){
                everySpace[currentIndex] = new Space(j, i);
                ++currentIndex;
            }
        }
        return everySpace;
    }

    public void restoreDefaults(){
        this.board = new Piece[height][width];
        gameOver = false;
        victory = false;
        firstClick = true;
        flagCount = 0;
        boardUpdates.clear();
    }

    /**
     * Handle standard game clicks
     * if game is over, do nothing. Otherwise, handle all possible ClickActions
     *
     * @param space  Space clicked
     * @param action Type of click
     */
    @Override
    public void handleClick(Space space, ClickAction action) {

        if(gameOver) { return;}

        switch (action) {
            case DEFAULT:
                handleDefaultClick(space);
                break;
            case FLAG:
                handleFlagClick(space);
                break;
            case MARK:
                handleMarkClick(space);
                break;
            case CHEAT:
                handleCheatClick(space);
                break;
        }
    }

    private void handleDefaultClick(Space space) {
        if(firstClick){
            firstClick(space);
        }

         if(isNumber(space)){
            //get neighbors of the space
            //iterate through the neighbors and see how many are flags
             Space[] neighbors = getNeighbors(space);

             var currentFlags = 0;
             for(Space neighbor : neighbors){
                 if(getPiece(neighbor) == Piece.FLAG){
                     ++currentFlags;
                 }
             }
             if(currentFlags == getPiece(space).getNumber()){
                 for(Space neighbor : neighbors){
                     activateSpace(neighbor);
                 }
             }

        } else {
            activateSpace(space);
        }

         checkVictory();

    }

    private void checkVictory() {
        for(Space space : everySpace()){
            if(!isMine(space)){
                Piece piece = getPiece(space);
                if(piece == null || !piece.isNumber()){
                    return;
                }
            }
        }
        victory = true;
        gameOver = true;
        showMines(null,true);
    }
    
    public boolean isMine(Space space){
        if (!isValid(space)) {
            return false;
        }
        return mines[space.getY()][space.getX()];
    }

    public Space[] getNeighbors(Space space){
        if(!isValid(space)){
            return new Space[0];
        }

        Space[] neighbors = new Space[8];
        var x = space.getX();
        var y = space.getY();
        var neighborCount = 0;
        for(var i = -1; i <= 1; i++){
            for(var j = -1; j <= 1; j++){
                if(i == 0 && j == 0){
                    continue;
                }
                Space neighbor = new Space(x+i, y+j);
                if(isValid(neighbor)){
                    neighbors[neighborCount] = neighbor;
                    ++neighborCount;
                }
            }
        }
        return neighbors;
    }

    public void activateSpace(Space space){
        if(!isReplaceable(space)){
            return;
        }
        if(isMine(space)){
            loseGame(space);
        } else {
            Piece tempPiece = getNumber(space);
            setPiece(space, tempPiece);
            if(tempPiece == Piece.N0){
                for(Space neighbor : getNeighbors(space)){
                    activateSpace(neighbor);
                }
            }
        }
    }

    private void loseGame(Space space) {
        gameOver = true;
        showMines(space, false);
    }

    private void showMines(Space thisSpace, boolean victory) {
        Piece mine = Piece.MINE_RED;
        if(victory){
           mine = Piece.MINE_GREEN;
        }

       for(Space space : everySpace()){
           if(space.equals(thisSpace)){
               setPiece(space, mine);
           }
           Piece piece = getPiece(space);
           if(isMine(space)){
               if(piece == null || piece.isMark()){
                   setPiece(space, mine);
               }
           } else {
               if(piece == Piece.FLAG){
                   setPiece(space, Piece.FLAG_NOT);
               }
           }
       }
    }

    private Piece getNumber(Space space) {
        if(!isValid(space)){
            return null;
        }
        var count = 0;
        for(Space neighbor : getNeighbors(space)){
            if(isMine(neighbor)){
                ++count;
            }
        }
        return Piece.fromNumber(count);
    }

    private boolean isNumber(Space space) {
        if (!isValid(space)) {
            return false;
        }

        var piece = getPiece(space);

        if (piece == null) {
            return false;
        }

        return piece.isNumber();
    }

    private void handleFlagClick(Space space) {

            var piece = getPiece(space);
            if (piece == Piece.FLAG) {
                setPiece(space, Piece.MARK_RED);
                --flagCount;
                markUpdate(space);
            } else if (piece != null && piece.isMark()) {
                setPiece(space, null);
                markUpdate(space);
            } else if (isReplaceable(space) && flagCount < mineCount) {
                setPiece(space, Piece.FLAG);
                markUpdate(space);
                ++flagCount;
            }

    }

    private void markUpdate(Space space) {
        if(!boardUpdates.contains(space)){
            boardUpdates.add(space);
        }
    }

    private void setPiece(Space space, Piece piece) {
        if(isValid(space)){
            board[space.getY()][space.getX()] = piece;
            markUpdate(space);
        }
    }

    private void handleMarkClick(Space space) {
        var piece = getPiece(space);
        if(piece == Piece.MARK_RED) {
            setPiece(space, Piece.MARK_GREEN);
            markUpdate(space);
        }
        else if (piece == Piece.MARK_GREEN) {
            setPiece(space, Piece.MARK_BLUE);
            markUpdate(space);
        }
        else if (piece == Piece.MARK_BLUE) {
            setPiece(space, Piece.MARK_RED);
            markUpdate(space);
        }
        else if (piece != null && piece.isMark()){
            setPiece(space,null);
            markUpdate(space);
        } else if (isReplaceable(space)) {
            setPiece(space, Piece.MARK_RED);
            markUpdate(space);
        }
    }

    private void handleCheatClick(Space space) {
    }

    private void firstClick(Space space) {
        generateMines(space);
        firstClick = false;
    }

    private void generateMines(Space thisSpace) {

        for(var i = 0; i < height; ++i){
            for(var j = 0; j < width; ++j){
                mines[i][j] = false;
            }
        }
        int thisMineCount = 0;
        while(thisMineCount < mineCount)
        {
            Random random = new Random();
            int randomHeight = random.nextInt(getHeight());
            int randomWidth = random.nextInt(getWidth());

            if(thisSpace.getX() == randomWidth && thisSpace.getY() == randomHeight) {
                continue;
            } else if (mines[randomHeight][randomWidth] == true){
                continue;
            } else {
                mines[randomHeight][randomWidth] = true;
                ++thisMineCount;
            }

            Space[] neighbors = getNeighbors(thisSpace);

            for(Space neighbor : neighbors){
                if(isValid(neighbor)) {
                    if (neighbor.getY() == randomHeight && neighbor.getX() == randomWidth) {
                        mines[randomHeight][randomWidth] = false;
                        --thisMineCount;
                    }
                }
            }
        }
    }

    /**
     * Gets the piece on the specified space
     * If the space is invalid, return null, otherwise, return the piece
     * on the specified space
     *
     * @param space Space to check for a piece
     * @return the piece on the specified space
     */
    @Override
    public Piece getPiece(Space space) {
        if(!isValid(space)){
            return null;
        }
        return board[space.getY()][space.getX()];
    }

    /**
     * Determines if a space appears enabled, or pressed up
     * If a space is invalid, return false, otherwise, get the piece
     * If the piece is null, that means the space is empty and pops up, so return true
     * Default, just return piece.isEnabled() value
     *
     * @param space Space to check
     * @return True if the space appears enabled
     */
    @Override
    public boolean isEnabled(Space space) {
        if(!isValid(space)){
            return false;
        }

        var piece = getPiece(space);

        if(piece == null){
            return true;
        }
        else{
            return piece.isEnabled();
        }
    }

    public boolean isReplaceable(Space space) {
        if(!isValid(space)){
            return false;
        }

        var piece = getPiece(space);

        if(piece == null){
            return true;
        }
        else{
            return piece.isReplaceable();
        }
    }

    /**
     * Gets the width of the board
     *
     * @return The width of the board
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the height of the board
     *
     * @return The height of the board
     */
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the number of mines left on the board. This should equal
     * the mineCount - flagCount
     *
     * @return number of mines remaining
     */
    @Override
    public int getRemainingMines() {
        return mineCount - flagCount;
    }

    /**
     * Checks if the player has used their cheat
     *
     * @return if the player has cheats left (cheats > 0)
     */
    @Override
    public boolean canCheat() {
        return cheats > 0;
    }

    /**
     * Checks how many cheats the player has left
     *
     * @return remaining cheats
     */
    @Override
    public int getCheats() {
        return cheats;
    }

    /**
     * Checks if the game ended
     *
     * @return true if the game ended (gameOver)
     */
    @Override
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * Checks if the player won the game
     *
     * @return true if the player won the game (victory)
     */
    @Override
    public boolean hasWon() {
        return victory;
    }

    /**
     * Checks if a space is within the bounds of this board. This entails:
     * If the space is null, x < 0 OR y < 0, x >= height OR y < 0, then it's not valid
     * Otherwise, return true
     *
     * @param space Space to check
     * @return true if the space is within this board
     */
    @Override
    public boolean isValid(Space space) {
        if(space == null){
            return false;
        }

        if(space.getX() < 0 || space.getY() < 0){
            return false;
        }

        return space.getY() < height && space.getX() < width;
    }

    /**
     * Gets a list of spaces pending updates to update the button displays
     *
     * @return a list of spaces needing an update (boardUpdates)
     */
    @Override
    public Queue<Space> getUpdates() {
        return boardUpdates;
    }
}
