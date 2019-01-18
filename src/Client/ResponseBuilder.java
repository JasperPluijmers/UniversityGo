package Client;

public class ResponseBuilder {

    public static String setConfig(int gameId, int colour, int boardSize) {
        return "SET_CONFIG" + "+" + gameId + "+" + colour + "+" + boardSize;
    }

    public static String pass(int gameId, String userName) {
        return "PASS" + "+" + gameId + "+" + userName;
    }

    public static String move(int gameId, String userName, String index) {
        return "MOVE" + "+" + gameId + "+" + userName + "+" + index;
    }
}
