package go.controller;


import go.utility.TerminalPlayer;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GameTest {

    @Test
    public void koTest() {
        Game game = new Game(5, Arrays.asList(new TerminalPlayer(1),new TerminalPlayer(2)));

        assertTrue(game.playMove("PLAY 1",1));
        assertTrue(game.playMove("PLAY 10",2));
        assertTrue(game.playMove("PLAY 7",1));
        assertTrue(game.playMove("PLAY 12",2));
        assertTrue(game.playMove("PLAY 5",1));
        assertTrue(game.playMove("PLAY 16",2));
        assertTrue(game.playMove("PLAY 11",1));
        assertTrue(game.playMove("PLAY 6",2));
        assertFalse(game.playMove("PLAY 11",1));

    }
}
