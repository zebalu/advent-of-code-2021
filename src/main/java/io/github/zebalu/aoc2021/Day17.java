package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day17 {
    public static void main(String[] args) {
        Area target = Area.fromString(INPUT);
        firstPart(target);
        secondPart(target);
    }

    private static void firstPart(Area target) {
        int maxY = target.velocityCandidates().stream().mapToInt(c -> testTrajectory(c.x(), c.y(), target)).max()
                .orElseThrow();
        System.out.println(maxY);
    }

    private static void secondPart(Area target) {
        long count = target.velocityCandidates().stream().mapToInt(c -> testTrajectory(c.x(), c.y(), target))
                .filter(y -> y > Integer.MIN_VALUE).count();
        System.out.println(count);
    }

    private static int testTrajectory(int x, int y, Area target) {
        Coord velocity = new Coord(x, y);
        Coord position = new Coord(0, 0);
        int maxY = Integer.MIN_VALUE;
        while (target.before(position)) {
            if (maxY < position.y()) {
                maxY = position.y();
            }
            position = position.plus(velocity);
            velocity = velocity.slowDown();
        }
        if (target.contains(position)) {
            return maxY;
        }
        return Integer.MIN_VALUE;
    }

    private static record Coord(int x, int y) {
        Coord slowDown() {
            int nX = x > 0 ? x - 1 : x < 0 ? x + 1 : 0;
            int nY = y - 1;
            return new Coord(nX, nY);
        }

        Coord plus(Coord coord) {
            return new Coord(x + coord.x, y + coord.y);
        }
    }

    private static record Area(Coord topLeft, Coord bottomRight) {
        private static final Pattern AREA_PATTERN = Pattern
                .compile("^target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)$");

        boolean contains(Coord coord) {
            return topLeft.x() <= coord.x() && coord.x() <= bottomRight.x() && coord.y() <= topLeft.y()
                    && bottomRight.y() <= coord.y();
        }

        boolean before(Coord coord) {
            return (coord.x() < topLeft.x() && bottomRight.y() < coord.y())
                    || (coord.x() < bottomRight.x() && topLeft.y() < coord.y());
        }

        List<Coord> velocityCandidates() {
            List<Coord> result = new ArrayList<>(10_000);
            for (int x = 0; x < bottomRight.x() * 2; ++x) {
                for (int y = bottomRight.y() * 2; y < -bottomRight().y() * 2; ++y) {
                    result.add(new Coord(x, y));
                }
            }
            return result;
        }

        static Area fromString(String desc) {
            var m = AREA_PATTERN.matcher(desc);
            if (!m.matches()) {
                throw new IllegalArgumentException("does not match: " + desc);
            }
            int minX = Integer.parseInt(m.group(1));
            int maxX = Integer.parseInt(m.group(2));
            int minY = Integer.parseInt(m.group(3));
            int maxY = Integer.parseInt(m.group(4));
            return new Area(new Coord(minX, maxY), new Coord(maxX, minY));
        }
    }

    private static final String INPUT = """
            target area: x=230..283, y=-107..-57""";
}
