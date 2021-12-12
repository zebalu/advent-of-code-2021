package io.github.zebalu.aoc2021;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Day12 {
    public static void main(String[] args) {
        Map<String, Set<String>> graph = readGraph();
        firstPart(graph);
        secondPart(graph);
    }

    private static void firstPart(Map<String, Set<String>> graph) {
        System.out.println(countPathes(graph, false));
    }

    private static void secondPart(Map<String, Set<String>> graph) {
        System.out.println(countPathes(graph, true));
    }

    private static int countPathes(Map<String, Set<String>> graph, boolean doublingAllowed) {
        Queue<Past> pathes = new LinkedList<>();
        int found = 0;
        pathes.add(new Past("start"));
        while (!pathes.isEmpty()) {
            var top = pathes.poll();
            if (!top.isEnd()) {
                pathes.addAll(top.extend(graph.get(top.currentPosition()), doublingAllowed));
            } else {
                ++found;
            }
        }
        return found;
    }

    private static Map<String, Set<String>> readGraph() {
        Map<String, Set<String>> graph = new HashMap<>();
        INPUT.lines().forEach(line -> {
            var parts = line.split("-");
            graph.computeIfAbsent(parts[0], k -> new HashSet<>()).add(parts[1]);
            graph.computeIfAbsent(parts[1], k -> new HashSet<>()).add(parts[0]);
        });
        return graph;
    }

    private static class Past {
        private final Set<String> visitedCaves = new HashSet<>();
        private final String doubleSmall;
        private final String lastVisited;

        Past(String step) {
            this(new HashSet<>(), step, null);
        }

        private Past(Set<String> caves, String step, String doubleSmall) {
            this.visitedCaves.addAll(caves);
            this.doubleSmall = doubleSmall == null ? determineDouble(step) : doubleSmall;
            this.visitedCaves.add(step);
            this.lastVisited = step;
        }

        String currentPosition() {
            return lastVisited;
        }

        Past extend(String step) {
            return new Past(visitedCaves, step, doubleSmall);
        }

        List<Past> extend(Set<String> possibilities, boolean doublingAllowed) {
            return possibilities.stream().filter(c -> canExtend(c, doublingAllowed)).map(this::extend).toList();
        }

        boolean canExtend(String cave, boolean doublingAllowed) {
            return isBig(cave) || (!cave.equals("start")
                    && (!visitedCaves.contains(cave) || (doublingAllowed && doubleSmall == null)));
        }

        boolean isEnd() {
            return "end".equals(lastVisited);
        }

        boolean isBig(String cave) {
            return cave.equals(cave.toUpperCase(Locale.ROOT));
        }

        private String determineDouble(String candidate) {
            if (!isBig(candidate) && visitedCaves.contains(candidate)) {
                return candidate;
            }
            return null;
        }
    }

    private static final String INPUT = """
            ln-nr
            ln-wy
            fl-XI
            qc-start
            qq-wy
            qc-ln
            ZD-nr
            qc-YN
            XI-wy
            ln-qq
            ln-XI
            YN-start
            qq-XI
            nr-XI
            start-qq
            qq-qc
            end-XI
            qq-YN
            ln-YN
            end-wy
            qc-nr
            end-nr""";
}
