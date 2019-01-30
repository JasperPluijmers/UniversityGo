package server.utilities;

/**
 * All possible commands the server can recieve from the client. Support is built in for the string representation for these commands.
 */
public enum ClientCommand {
    HANDSHAKE("HANDSHAKE"),
    SET_CONFIG("SET_CONFIG"),
    MOVE("MOVE"),
    SET_REMATCH("SET_REMATCH"),
    EXIT("EXIT");

    private String name;

    ClientCommand(String name) {
        this.name = name;
    }

    /**
     * Returns a ClientCommand corresponding to the specified name, or throws an exception if the name does not
     * correspond to any ClientCommand.
     * @param name name to search for
     * @return
     */
    public static ClientCommand fromString(String name) {
        for (ClientCommand clientCommand : ClientCommand.values()) {
            if (clientCommand.name.equals(name)) {
                return clientCommand;
            }
        }
        throw new IllegalArgumentException("No name in enum found corresponding to name: " + name);
    }
}
