package go.controller;


import go.utility.TerminalPlayer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GameTest {
    Game game;

    @Before
    public void setup() {
        game = new Game(5, Arrays.asList(new TerminalPlayer(1),new TerminalPlayer(2)));
    }

    @Test
    public void koTest() {
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

    @Test
    public void koTest2() {
        assertTrue(game.playMove("PLAY 1",1));
        assertTrue(game.playMove("PASS",2));
        assertTrue(game.playMove("PLAY 5",1));
        assertFalse(game.playMove("PLAY 0",2));
        assertTrue(game.playMove("PLAY 2", 2));
        assertTrue(game.playMove("PLAY 3",1));
        assertFalse(game.playMove("PLAY 0", 2));
    }

    @Test
    public void sameSpotTest() {
        assertTrue(game.playMove("PLAY 0", 1));
        assertFalse(game.playMove("PLAY 0",2));
    }


}
