package client.utilities;

public enum ServerCommand {
    ACKNOWLEDGE_HANDSHAKE("ACKNOWLEDGE_HANDSHAKE"),
    ACKNOWLEDGE_CONFIG("ACKNOWLEDGE_CONFIG"),
    ACKNOWLEDGE_MOVE("ACKNOWLEDGE_MOVE"),
    INVALID_MOVE("INVALID_MOVE"),
    REQUEST_CONFIG("REQUEST_CONFIG"),
    GAME_FINISHED("GAME_FINISHED"),
    REQUEST_REMATCH("REQUEST_REMATCH"),
    ACKNOWLEDGE_REMATCH("ACKNOWLEDGE_REMATCH");

    private String name;

    ServerCommand(String name) {
        this.name = name;
    }

    public static ServerCommand fromString(String name) {
        for (ServerCommand clientCommand : ServerCommand.values()) {
            if (clientCommand.name.equals(name)) {
                return clientCommand;
            }
        }
        throw new IllegalArgumentException("No name in enum found corresponding to name: " + name);
    }

}
