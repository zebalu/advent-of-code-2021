package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class Day21 {
    public static void main(String[] args) {
        var positions = INPUT.lines().mapToInt(s -> Integer.parseInt(s.split(" position: ")[1])).toArray();
        firstPart(positions);
        secondPart(positions);
    }

    private static void secondPart(int[] positions) {
        System.out.println(plays(positions[0], positions[1], 0, 0, true).getWinner());
        System.out.println("iterative: "+iterativePlays(positions));
    }
    
    private static long iterativePlays(int[] positions) {
        Queue<State> queue = new LinkedList<>();
        queue.add(new State(positions[0], positions[1], 0, 0, 1L, true));
        long p1Wins = 0L;
        long p2Wins = 0L;   
        while(!queue.isEmpty()) {
            State top = queue.poll();
            if(top.p1Score>=21) {
                p1Wins+=top.ways;
            } else if(top.p2Score >=21) {
                p2Wins+=top.ways;
            } else if(top.p1Comes) {
                fromToReachableCountMap.get(top.p1Pos).forEach(pc -> {
                    queue.add(new State(pc.toPosition, top.p2Pos, top.p1Score+pc.toPosition, top.p2Score, top.ways*pc.count, !top.p1Comes));
                });
            } else {
                fromToReachableCountMap.get(top.p2Pos).forEach(pc -> {
                    queue.add(new State(top.p1Pos, pc.toPosition, top.p1Score, top.p2Score+pc.toPosition, top.ways*pc.count, !top.p1Comes));
                });
            }
        }
        if(p1Wins<p2Wins) {
            return p2Wins;
        } 
        return p1Wins;

    }
    
    private static record State(int p1Pos, int p2Pos, int p1Score, int p2Score, long ways, boolean p1Comes) {
        
    }

    private static Wins plays(int p1Pos, int p2Pos, int p1Score, int p2Score, boolean p1Comes) {
        if (p1Comes) {
            if (21 <= p2Score) {
                return new Wins(0L, 1L);
            }
            return fromToReachableCountMap.get(p1Pos).stream()
                    .map(e -> plays(e.toPosition(), p2Pos, p1Score + e.toPosition(), p2Score, !p1Comes).mul(e.count()))
                    .reduce((a, v) -> a.add(v)).orElseThrow();
        } else {
            if (21 <= p1Score) {
                return new Wins(1L, 0L);
            }
            return fromToReachableCountMap.get(p2Pos).stream()
                    .map(e -> plays(p1Pos, e.toPosition, p1Score, p2Score + e.toPosition, !p1Comes).mul(e.count()))
                    .reduce((a, v) -> a.add(v)).orElseThrow();
        }
    }

    private static final List<Integer> diracDieRolls = generateDiracDieRolls();
    private static final Map<Integer, List<Integer>> fromToReachableMap = generateFromToreachableMap();
    private static final Map<Integer, List<PositionCount>> fromToReachableCountMap = generateFromToReachableCountMap();

    private static List<Integer> generateDiracDieRolls() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            for (int j = 1; j <= 3; ++j) {
                for (int k = 1; k <= 3; ++k) {
                    result.add(i + j + k);
                }
            }
        }
        return result;
    }

    private static Map<Integer, List<Integer>> generateFromToreachableMap() {
        Map<Integer, List<Integer>> fromToReachableMap = new HashMap<>();
        for (int i = 1; i <= 10; ++i) {
            int p = i;
            var reachable = fromToReachableMap.computeIfAbsent(i, k -> new ArrayList<>());
            reachable.addAll(diracDieRolls.stream().map(r -> (r + p - 1) % 10 + 1).toList());
        }
        return fromToReachableMap;
    }

    private static Map<Integer, List<PositionCount>> generateFromToReachableCountMap() {
        Map<Integer, List<PositionCount>> result = new HashMap<>();
        fromToReachableMap.entrySet().stream()
                .map(e -> Map.entry(e.getKey(),
                        e.getValue().stream().collect(Collectors.groupingBy(i -> i, Collectors.counting())).entrySet()
                                .stream().map(e2 -> new PositionCount(e2.getKey(), e2.getValue())).toList()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    private static record Wins(long p1Wins, long p2Wins) {
        Wins add(Wins o) {
            return new Wins(p1Wins + o.p1Wins, p2Wins + o.p2Wins);
        }

        Wins mul(long val) {
            return new Wins(val * p1Wins, val * p2Wins);
        }

        long getWinner() {
            if (p1Wins < p2Wins) {
                return p2Wins;
            }
            return p1Wins;
        }
    }

    private static record PositionCount(int toPosition, long count) {
    }

    private static void firstPart(int[] positions) {
        RoundNum dice = new RoundNum(100);
        Player player1 = new Player(1, positions[0]);
        Player player2 = new Player(2, positions[1]);
        int selector = 0;
        while (player1.score < 1000 && player2.score < 1000) {
            Player player = selector % 2 == 0 ? player1 : player2;
            player.roll(dice);
            ++selector;
        }
        Player player = selector % 2 == 0 ? player1 : player2;
        System.out.println(player.score * dice.getRollCount());
    }

    private static class Player implements Cloneable {
        final int id;
        RoundNum position = new RoundNum(10);
        int score = 0;

        public Player(int id, int startPosition) {
            this.id = id;
            position.set(startPosition);
        }

        int roll(RoundNum dice) {
            int step = dice.next() + dice.next() + dice.next();
            return roll(step);
        }

        int roll(int rolledSum) {
            int step = rolledSum;
            position.add(step);
            score += position.get();
            return score;
        }

        @Override
        public Player clone() {
            try {
                Player clone = (Player) super.clone();
                clone.position = position.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Player p) {
                return p.id == id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    private static class RoundNum implements Cloneable {
        final int max;
        int nextCount = 0;

        public RoundNum(int max) {
            this.max = max;
        }

        int i = 1;

        int next() {
            ++nextCount;
            int ret = i++;
            i = (i - 1) % max + 1;
            return ret;
        }

        int add(int num) {
            i = (i + num - 1) % max + 1;
            return i;
        }

        void set(int v) {
            i = v;
        }

        int get() {
            return i;
        }

        int getRollCount() {
            return nextCount;
        }

        @Override
        public RoundNum clone() {
            try {
                return (RoundNum) super.clone();
            } catch (CloneNotSupportedException cnse) {
                throw new IllegalStateException(cnse);
            }
        }
    }

    private static final String INPUT = """
            Player 1 starting position: 10
            Player 2 starting position: 1""";
}
