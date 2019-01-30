package go.utility;

/**
 * Colour enum, all uses of colour in the program are part of this enum.
 */
public enum Colour {
    EMPTY(0),
    BLACK(1),
    WHITE(2),
    //Green is currently used as hint stone in the gui
    GREEN(-3);

    private final int value;

    Colour(int value) {
        this.value = value;
    }

    /**
     * Returns Colour enum of the value asked.
     * @param value 0, 1 or 2 corresponding to EMPTY, BLACK or WHITE
     * @return Colour enum
     */
    public static Colour getByInt(int value) {
        for (Colour colour : Colour.values()) {
            if (colour.value == value) {
                return colour;
            }
        }
        throw new IllegalArgumentException("No value in enum found corresponding to value: " + value);
    }

    public int getValue() {
        return this.value;
    }
}
