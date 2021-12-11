package io.github.zebalu.aoc2021;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;

public class Day11 {
    public static void main(String[] args) {
        firstPart();
        secondPart();
    }

    private static void firstPart() {
        OctopusMap oMap = readOctopuses();
        System.out.println(IntStream.iterate(0, i -> i + 1).limit(100).map(i -> oMap.step()).sum());
    }

    private static void secondPart() {
        OctopusMap oMap = readOctopuses();
        System.out.println(
                IntStream.iterate(0, i -> i + 1).peek(i -> oMap.step()).takeWhile(i -> !oMap.isSyncron()).count() + 1);
    }

    private static OctopusMap readOctopuses() {
        OctopusMap oMap = new OctopusMap();
        var lines = INPUT.lines().toList();
        for (int y = 0; y < lines.size(); ++y) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); ++x) {
                oMap.put(new Coord(x, y), Integer.parseInt(line.substring(x, x + 1)));
            }
        }
        return oMap;
    }

    private static record Coord(int x, int y) {
        List<Coord> adjecents() {
            return List.of(new Coord(x - 1, y - 1), new Coord(x - 1, y), new Coord(x - 1, y + 1), new Coord(x, y - 1),
                    new Coord(x, y + 1), new Coord(x + 1, y - 1), new Coord(x + 1, y), new Coord(x + 1, y + 1));
        }
    }

    private static class OctopusMap extends HashMap<Coord, Integer> {
        private static final long serialVersionUID = 1L;

        int step() {
            Set<Coord> flashers = new HashSet<>();
            Queue<Coord> toIncrease = new LinkedList<>();
            toIncrease.addAll(keySet());
            while (!toIncrease.isEmpty()) {
                var top = toIncrease.poll();
                if (!flashers.contains(top)) {
                    var val = compute(top, (k, v) -> v == null ? null : v + 1);
                    if (val > 9) {
                        flashers.add(top);
                        top.adjecents().stream().filter(c -> containsKey(c) && !flashers.contains(c))
                                .forEach(toIncrease::add);
                    }
                }
            }
            flashers.stream().forEach(c -> put(c, 0));
            return flashers.size();
        }

        boolean isSyncron() {
            return values().stream().allMatch(i -> i == 0);
        }

    }

    private static final String INPUT = """
            1172728874
            6751454281
            2612343533
            1884877511
            7574346247
            2117413745
            7766736517
            4331783444
            4841215828
            6857766273""";
}
