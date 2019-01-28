package server.utilities;

public enum ClientCommand {
    HANDSHAKE ("HANDSHAKE"),
    SET_CONFIG ("SET_CONFIG"),
    MOVE ("MOVE"),
    SET_REMATCH ("SET_REMATCH"),
    EXIT ("EXIT");

    private String name;

    ClientCommand(String name) {
        this.name = name;
    }

    public static ClientCommand fromString(String name) {
        for (ClientCommand clientCommand : ClientCommand.values()) {
            if (clientCommand.name.equals(name)) {
                return clientCommand;
            }
        }
        throw new IllegalArgumentException("No name in enum found corresponding to name: " + name);
    }
}
