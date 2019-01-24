package go.utility;

public enum Colour {
    EMPTY (0),
    BLACK (1),
    WHITE (2);

    private final int value;
    Colour(int value) {
        this.value = value;
    }

    public static Colour getByInt(int value) {
        for (Colour colour : Colour.values()) {
            if (colour.value == value) {
                return colour;
            }
        }
        //exception maybe?
        return null;
    }

    public int getValue() {
        return this.value;
    }
}
