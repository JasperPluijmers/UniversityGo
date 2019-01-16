package go.controller;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import go.utility.TerminalPlayer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class GameTest {

    @Test
    public void koTest() {
        Game game = new Game(5, Arrays.asList(new TerminalPlayer(1),new TerminalPlayer(2)));

        game.playMove("PLAY 1",1);
        game.playMove("PLAY 10",2);
        game.playMove("PLAY 7",1);
        game.playMove("PLAY 12",2);
        game.playMove("PLAY 5",1);
        game.playMove("PLAY 16",2);
        game.playMove("PLAY 11",1);
        System.out.println(game.getBoard());
        game.playMove("PLAY 6",2);
        System.out.println(game.getBoard());
        game.playMove("PLAY 11",1);
        System.out.println(game.getBoard());
        //game.playMove();

    }
}
