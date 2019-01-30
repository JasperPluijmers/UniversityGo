package go.controller;


import go.model.Board;
import go.model.GameState;
import go.utility.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for a game of go, this class handles the turns and stops the game if someone wins.
 */
public class Game {

    private GameState state;

    /**
     * Constructs a Game object, which in turn constructs a gamestate with a new board.
     *
     * @param dimension The size of the board of the game
     * @param players   List of players in the game, first player in this list plays as black, the second one plays as white
     */
    public Game(int dimension, List<Player> players) {
        this.state = new GameState(new Board(dimension), players);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setGame(this);
            players.get(i).setColour(Colour.getByInt(i + 1));
        }
    }

    /**
     * Changes status to playing and initiates the first move
     */
    public void play() {
        state.setStatus(Status.PLAYING);
        nextTurn();
    }

    /**
     * Notifies players they need to play a move or finishes the game if two players passed
     */
    public void nextTurn() {
        switch (state.getStatus()) {
            case PLAYING:
                state.currentPlayer().requestMove(state.getBoard());
                break;
            case FINISHED:
                Map<Colour, Double> finalScore = Score.score(state.getBoard());
                int winner = finalScore.get(Colour.BLACK) > finalScore.get(Colour.WHITE) ? 1 : 2;
                for (Player player : state.getPlayers()) {
                    player.finishGame(state.getPlayers().get(winner - 1).getUsername(), finalScore, "Two passes");
                }
                break;
        }
    }

    /**
     * Notifies the players that a move has been played
     *
     * @param move   The move that has been played
     * @param colour The colour of the move that has been played
     */
    public void acknowledgeMove(int move, Colour colour) {
        for (Player player : state.getPlayers()) {
            player.acknowledgeMove(move, colour);
        }
    }

    /**
     * Plays a certain move on the board.
     * Returns true if a move has been played or false if if the move was not played because it was not valid
     *
     * @param move   Move that has to be played, either "PASS" or "PLAY n" where n should be an integer
     * @param colour
     * @return
     */
    public boolean playMove(String move, Colour colour) {
        //Logic for passing
        if (move.equals("PASS")) {

            //If the last move was also a pass, finish the game, otherwise flag last move as pass
            if (state.getPassed() == true) {
                state.setStatus(Status.FINISHED);
            } else {
                state.setPassed(true);
            }
            //change currentplayer and acknowledge the move to the players, then starts next move
            state.nextPlayer();
            acknowledgeMove(-1, colour);
            nextTurn();
            return true;

            //Logic for playing a normal move
        } else if (move.matches("(PLAY )\\d*")) {
            //Get the move from the string
            int moveNumber = Integer.parseInt(move.split(" ")[1]);

            //Validates the move
            if (MoveValidator.validateMove(moveNumber, colour, state.getBoard())) {

                //Update the board
                state.getBoard().setEntry(moveNumber, colour);
                state.getBoard().updateHistory();
                BoardUpdater.updateBoard(moveNumber, state.getBoard());

                //Change currentplayer and flag last move as not a pass
                state.nextPlayer();
                state.setPassed(false);
                acknowledgeMove(moveNumber, colour);
                nextTurn();
                return true;
            } else {
                //Notifies players that a wrong move has been played
                state.currentPlayer().wrongMove();
            }

        } else {
            //Command in a complete different form, not valid for playing a move
            System.out.println("Command not recognized");
        }
        return false;
    }

    public GameState getState() {
        return this.state;
    }


}
