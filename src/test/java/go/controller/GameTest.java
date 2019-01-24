package go.controller;


import go.utility.Colour;
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
        assertTrue(game.playMove("PLAY 1", Colour.BLACK));
        assertTrue(game.playMove("PLAY 10",Colour.WHITE));
        assertTrue(game.playMove("PLAY 7",Colour.BLACK));
        assertTrue(game.playMove("PLAY 12",Colour.WHITE));
        assertTrue(game.playMove("PLAY 5",Colour.BLACK));
        assertTrue(game.playMove("PLAY 16",Colour.WHITE));
        assertTrue(game.playMove("PLAY 11",Colour.BLACK));
        assertTrue(game.playMove("PLAY 6",Colour.WHITE));
        assertFalse(game.playMove("PLAY 11",Colour.BLACK));

    }

    @Test
    public void koTest2() {
        assertTrue(game.playMove("PLAY 1",Colour.BLACK));
        assertTrue(game.playMove("PASS",Colour.WHITE));
        assertTrue(game.playMove("PLAY 5",Colour.BLACK));
        assertFalse(game.playMove("PLAY 0",Colour.WHITE));
        assertTrue(game.playMove("PLAY 2", Colour.WHITE));
        assertTrue(game.playMove("PLAY 3",Colour.BLACK));
        assertFalse(game.playMove("PLAY 0", Colour.WHITE));
    }

    @Test
    public void sameSpotTest() {
        assertTrue(game.playMove("PLAY 0", Colour.BLACK));
        assertFalse(game.playMove("PLAY 0",Colour.WHITE));
    }


}
