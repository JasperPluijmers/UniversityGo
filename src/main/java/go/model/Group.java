package go.model;

import go.utility.Colour;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {

    private Map<Colour, Set<Integer>> neighbours;
    private Set<Integer> groupMembers;
    private Colour colour;

    public Group(Colour colour) {
        this.colour = colour;
        this.neighbours = new HashMap<>();
        this.groupMembers = new HashSet<>();

        this.neighbours.put(Colour.EMPTY, new HashSet<>());
        this.neighbours.put(Colour.BLACK, new HashSet<>());
        this.neighbours.put(Colour.WHITE, new HashSet<>());
    }

    public Colour getColour() {
        return this.colour;
    }

    public Set<Integer> getGroupMembers() {
        return this.groupMembers;
    }

    public Map<Colour, Set<Integer>> getNeighbours() {
        return this.neighbours;
    }

    public void addGroupMember(int index) {
        this.groupMembers.add(index);
    }

    public void addNeighbour(int index, Colour colour) {
        neighbours.get(colour).add(index);

    }

    public int freedoms() {
        return neighbours.get(Colour.EMPTY).size();

    }

    @Override
    public String toString() {
        return String.format("Group of %s, containing:" + groupMembers, colour);
    }
}
