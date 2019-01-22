package go.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {

    private Map<Integer, Set<Integer>> neighbours;
    private Set<Integer> groupMembers;
    private int flavour;

    public Group(int flavour) {
        this.flavour = flavour;
        this.neighbours = new HashMap<>();
        this.groupMembers = new HashSet<>();

        this.neighbours.put(0,new HashSet<>());
        this.neighbours.put(1,new HashSet<>());
        this.neighbours.put(2,new HashSet<>());
    }

    public int getFlavour() {
        return this.flavour;
    }

    public Set<Integer> getGroupMembers() {
        return this.groupMembers;
    }

    public Map<Integer, Set<Integer>> getNeighbours() {
        return this.neighbours;
    }

    public void addGroupMember(int index) {
        this.groupMembers.add(index);
    }

    public void addNeighbour(int index, int flavour) {
        neighbours.get(flavour).add(index);

    }

    public int freedoms() {
        if (!neighbours.containsKey(0)) {
            return 0;
        } else {
            return neighbours.get(0).size();
        }
    }

    @Override
    public String toString() {
        return String.format("Group of %d, containing:" + groupMembers,flavour);
    }
}
