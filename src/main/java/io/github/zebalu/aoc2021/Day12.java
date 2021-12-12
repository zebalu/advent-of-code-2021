package io.github.zebalu.aoc2021;

import java.util.ArrayList;
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
        Queue<Path> pathes = new LinkedList<>();
        int found = 0;
        pathes.add(new Path("start"));
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

    private static class Path {
        private final List<String> steps = new ArrayList<>();
        private final Set<String> caves = new HashSet<>();
        private final String doubleSmall;

        Path(String step) {
            this(new ArrayList<>(), new HashSet<>(), step, null);
        }

        private Path(List<String> steps, Set<String> caves, String step, String doubleSmall) {
            this.steps.addAll(steps);
            this.caves.addAll(caves);
            this.steps.add(step);
            this.caves.add(step);
            if (doubleSmall == null) {
                this.doubleSmall = determineDouble(step);
            } else {
                this.doubleSmall = doubleSmall;
            }
        }

        String currentPosition() {
            return steps.get(steps.size() - 1);
        }

        Path extend(String step) {
            return new Path(steps, caves, step, doubleSmall);
        }
        
        List<Path> extend(Set<String> possibilities, boolean doublingAllowed) {
            return possibilities.stream().filter(c->canExtend(c, doublingAllowed)).map(this::extend).toList();
        }

        boolean canExtend(String cave, boolean doublingAllowed) {
            if (isBig(cave)) {
                return true;
            }
            if (cave.equals("start")) {
                return false;
            }
            if (doublingAllowed && caves.contains(cave) && doubleSmall == null) {
                return true;
            }
            return !caves.contains(cave);
        }

        boolean isEnd() {
            return currentPosition().equals("end");
        }

        boolean isBig(String cave) {
            return cave.equals(cave.toUpperCase(Locale.ROOT));
        }

        int countVisit(String cave) {
            return (int) steps.stream().filter(cave::equals).count();
        }

        private String determineDouble(String candidate) {
            if (!isBig(candidate)) {
                if (2 == countVisit(candidate)) {
                    return candidate;
                }
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
