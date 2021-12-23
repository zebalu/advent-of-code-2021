package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Day23 {
    public static void main(String[] args) {
        firstPart();
        secondPart();
    }

    private static void firstPart() {
        System.out.println(solve(State.read(INPUT, false), false));
    }

    private static void secondPart() {
        System.out.println(solve(State.read(INPUT, true), false));
    }

    private static int solve(State init, boolean printHistory) {
        Queue<State> queue = new PriorityQueue<>();
        Set<State> seen = new HashSet<>();
        queue.add(init);
        seen.add(queue.peek());
        while (!queue.isEmpty() && !queue.peek().isEndState()) {
            var top = queue.poll();
            top.nextStates().forEach(s -> {
                if (!seen.contains(s)) {
                    queue.add(s);
                    seen.add(s);
                }
            });
        }
        if (printHistory) {
            queue.peek().printHistory();
        }
        return queue.peek().price();
    }

    private static record Amphipod(char type, int price, int moved) {

        static Amphipod fromChar(char c) {
            return switch (c) {
            case 'A' -> new Amphipod(c, 1, 0);
            case 'B' -> new Amphipod(c, 10, 0);
            case 'C' -> new Amphipod(c, 100, 0);
            case 'D' -> new Amphipod(c, 1000, 0);
            default -> throw new IllegalArgumentException("unkonwn char: " + c);
            };
        }

        int desiredRoom() {
            return switch (type) {
            case 'A' -> 0;
            case 'B' -> 1;
            case 'C' -> 2;
            case 'D' -> 3;
            default -> throw new IllegalStateException("unknown type: " + type);
            };
        }
    }

    private static record Room(Amphipod[] amphipods, int position) {
        static Room fromChars(char[] chars, int position) {
            Amphipod[] amphipods = new Amphipod[chars.length];
            for (int i = 0; i < amphipods.length; ++i) {
                amphipods[i] = Amphipod.fromChar(chars[i]);
            }
            return new Room(amphipods, position);
        }

        boolean canTake(Amphipod a) {
            return containsNull() && allNotNullIsFriendlyWith(a);
        }

        private boolean allNotNullIsFriendlyWith(Amphipod a) {
            for (int i = 0; i < amphipods.length; ++i) {
                if (amphipods[i] != null && amphipods[i].type != a.type) {
                    return false;
                }
            }
            return true;
        }

        private boolean containsNull() {
            for (int i = 0; i < amphipods.length; ++i) {
                if (amphipods[i] == null) {
                    return true;
                }
            }
            return false;
        }

        Amphipod topAmphipod() {
            if (shouldChange()) {
                for (int i = 0; i < amphipods.length; ++i) {
                    if (amphipods[i] != null) {
                        return amphipods[i];
                    }
                }
            }
            return null;
        }

        int moveOutPrice(Amphipod a) {
            for (int i = 0; i < amphipods.length; ++i) {
                if (amphipods[i] == a) {
                    return i + 1;
                }
            }
            throw new IllegalArgumentException("Amphipod: " + a + " is not in the room");
        }

        int moveInPrice() {
            for (int i = amphipods.length - 1; i >= 0; --i) {
                if (amphipods[i] == null) {
                    return i + 1;
                }
            }
            throw new IllegalArgumentException("There is no place in the room");
        }

        Room moveIn(Amphipod a) {
            for (int i = amphipods.length - 1; i >= 0; --i) {
                if (amphipods[i] == null) {
                    Amphipod[] copy = new Amphipod[amphipods.length];
                    System.arraycopy(amphipods, 0, copy, 0, amphipods.length);
                    copy[i] = a;
                    return new Room(copy, position);
                }
            }
            throw new IllegalArgumentException("There is no place in the room");
        }

        Room moveOut(Amphipod a) {
            for (int i = 0; i < amphipods.length; ++i) {
                if (amphipods[i] == a) {
                    Amphipod[] copy = new Amphipod[amphipods.length];
                    System.arraycopy(amphipods, 0, copy, 0, amphipods.length);
                    copy[i] = null;
                    return new Room(copy, position);
                }
            }
            throw new IllegalArgumentException("Amphipod: " + a + " is not in the room");
        }

        boolean shouldChange() {
            return hasBadAmphipod();
        }

        private boolean hasBadAmphipod() {
            boolean hasBad = false;
            for (int i = 0; i < amphipods.length && !hasBad; ++i) {
                if (amphipods[i] != null) {
                    hasBad |= isBad(amphipods[i]);
                }
            }
            return hasBad;
        }

        private boolean isBad(Amphipod a) {
            return switch (position) {
            case 2 -> a.type != 'A';
            case 4 -> a.type != 'B';
            case 6 -> a.type != 'C';
            case 8 -> a.type != 'D';
            default -> throw new IllegalStateException("unkonwn position: " + position);
            };
        }

        private String toString(Amphipod a) {
            if (a == null) {
                return ".";
            }
            return "" + a.type();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < amphipods.length; ++i) {
                sb.append(toString(amphipods[i]));
            }
            return sb.toString();
        }

        public int price() {
            int sum = 0;
            for (int i = 0; i < amphipods.length; ++i) {
                if (amphipods[i] != null) {
                    sum += amphipods[i].moved() * amphipods[i].price();
                }
            }
            return sum;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(amphipods);
            result = prime * result + Objects.hash(position);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Room other = (Room) obj;
            return Arrays.equals(amphipods, other.amphipods) && position == other.position;
        }

    }

    private static record State(Amphipod[] hallway, Room[] rooms, State previous) implements Comparable<State> {

        private static final Comparator<State> STATE_PRICE_COMPARATOR = Comparator.comparingInt(State::price);
        private static final Set<Integer> DONT_STAND_AT = Set.of(2, 4, 6, 8);

        List<State> nextStates() {
            List<State> result = new ArrayList<>();
            for (int i = 0; i < hallway.length; ++i) {
                var a = hallway[i];
                if (a != null && isFree(i, (a.desiredRoom() + 1) * 2) && rooms[a.desiredRoom()].canTake(a)) {
                    int move = Math.abs((a.desiredRoom() + 1) * 2 - i) + rooms[a.desiredRoom()].moveInPrice();
                    Amphipod moved = new Amphipod(a.type(), a.price(), a.moved() + move);
                    Amphipod[] h2 = new Amphipod[hallway.length];
                    System.arraycopy(hallway, 0, h2, 0, hallway.length);
                    h2[i] = null;
                    Room[] r2 = new Room[rooms.length];
                    System.arraycopy(rooms, 0, r2, 0, rooms.length);
                    r2[a.desiredRoom()] = rooms[a.desiredRoom()].moveIn(moved);
                    result.add(new State(h2, r2, this));
                }
            }
            for (int ri = 0; ri < rooms.length; ++ri) {
                var r = rooms[ri];
                if (r.shouldChange() && hallway[r.position] == null) {
                    Amphipod top = r.topAmphipod();
                    if (top != null) {
                        for (int i = 0; i < hallway.length; ++i) {
                            int moves = r.moveOutPrice(top);
                            if (i != r.position && isFree(r.position(), i) && !DONT_STAND_AT.contains(i)
                            /* && (i == top.desiredRoom() - 1 || i == top.desiredRoom() + 1) */) {
                                moves += Math.abs(r.position - i);
                                Amphipod moved = new Amphipod(top.type(), top.price(), top.moved() + moves);
                                Amphipod[] h2 = new Amphipod[hallway.length];
                                System.arraycopy(hallway, 0, h2, 0, hallway.length);
                                h2[i] = moved;
                                Room[] r2 = new Room[rooms.length];
                                System.arraycopy(rooms, 0, r2, 0, rooms.length);
                                r2[ri] = rooms[ri].moveOut(top);
                                result.add(new State(h2, r2, this));
                            }
                        }
                    }
                }
            }
            return result.stream().toList();
        }

        int price() {
            int sum = 0;
            for (var a : hallway) {
                if (a != null) {
                    sum += a.moved() * a.price();
                }
            }
            for (var r : rooms) {
                sum += r.price();
            }
            return sum;
        }

        boolean isEndState() {
            for (var a : hallway) {
                if (a != null) {
                    return false;
                }
            }
            for (var r : rooms) {
                if (r.shouldChange()) {
                    return false;
                }
            }
            return true;
        }

        private boolean isFree(int from, int to) {
            if (hallway[to] != null) {
                return false;
            }
            int change = (int) Math.signum(to - from);
            for (int i = from + change; i != to; i = i + change) {
                if (hallway[i] != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int compareTo(State o) {
            return STATE_PRICE_COMPARATOR.compare(this, o);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (var a : hallway) {
                if (a == null)
                    sb.append('.');
                else
                    sb.append(a.type);
            }
            sb.append("\n");
            for (int i = 0; i < rooms[0].amphipods.length; ++i) {
                sb.append("##" + rooms[0].toString(rooms[0].amphipods[i]) + "#"
                        + rooms[1].toString(rooms[1].amphipods[i]) + "#" + rooms[2].toString(rooms[2].amphipods[i])
                        + "#" + rooms[3].toString(rooms[3].amphipods[i]) + "##\n");

            }
            /*
             * sb.append("##"+rooms[0].toString(rooms[0].front)+"#"+rooms[1].toString(rooms[
             * 1].front)+"#"+rooms[2].toString(rooms[2].front)+"#"+rooms[3].toString(rooms[3
             * ].front)+"##\n");
             * sb.append("  "+rooms[0].toString(rooms[0].back)+" "+rooms[1].toString(rooms[1
             * ].back)+" "+rooms[2].toString(rooms[2].back)+" "+rooms[3].toString(rooms[3].
             * back)+" ");
             */
            /*
             * for (var r : rooms) { sb.append(r + " "); }
             */
            return sb.toString();
        }

        void printHistory() {
            Stack<State> s = new Stack<>();
            s.push(this);
            var current = this.previous;
            while (current != null) {
                s.push(current);
                current = current.previous;
            }
            int c = 0;
            while (!s.isEmpty()) {
                current = s.pop();
                System.out.println(++c + "\t" + current.price() + ":\n" + current + "\n\n");
            }
        }

        static State read(String data, boolean extend) {
            var lines = readLines(data, extend);
            var hallways = readHallways(lines);
            var rooms = readRooms(lines);
            return new State(hallways, rooms, null);
        }

        private static ArrayList<String> readLines(String data, boolean extend) {
            var lines = new ArrayList<>(data.lines().toList());
            if (extend) {
                lines.add(3, "#D#C#B#A#");
                lines.add(4, "#D#B#A#C#");
            }
            return lines;
        }

        private static Amphipod[] readHallways(ArrayList<String> lines) {
            int hallwayLength = 0;
            for (var c : lines.get(1).toCharArray()) {
                if (c == '.') {
                    ++hallwayLength;
                }
            }
            var hallways = new Amphipod[hallwayLength];
            return hallways;
        }

        private static Room[] readRooms(List<String> lines) {
            return convertToRooms(transposeRoom(readTransposedRooms(lines)));
        }

        private static Room[] convertToRooms(List<char[]> roomChars) {
            Room[] rooms = new Room[roomChars.size()];
            for (int i = 0; i < rooms.length; ++i) {
                rooms[i] = Room.fromChars(roomChars.get(i), (i + 1) * 2);
            }
            return rooms;
        }

        private static List<char[]> transposeRoom(List<List<Character>> roomLines) {
            List<char[]> roomChars = new ArrayList<>();
            for (int i = 0; i < roomLines.get(0).size(); ++i) {
                char[] chars = new char[roomLines.size()];
                for (int j = 0; j < chars.length; ++j) {
                    chars[j] = roomLines.get(j).get(i);
                }
                roomChars.add(chars);
            }
            return roomChars;
        }

        private static List<List<Character>> readTransposedRooms(List<String> lines) {
            List<List<Character>> roomLines = new ArrayList<>();
            for (int i = 2; i < lines.size() - 1; ++i) {
                List<Character> roomLine = new ArrayList<>();
                for (var c : lines.get(i).trim().toCharArray()) {
                    if (c != '#') {
                        roomLine.add(c);
                    }
                }
                roomLines.add(roomLine);
            }
            return roomLines;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof State s) {
                if (!Arrays.equals(hallway, s.hallway)) {
                    return false;
                }
                return Arrays.equals(rooms, s.rooms);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(hallway) * 23 + Arrays.hashCode(rooms);
        }
    }

    private static final String INPUT = """
            #############
            #...........#
            ###B#B#D#A###
              #D#C#A#C#
              #########""";

}
