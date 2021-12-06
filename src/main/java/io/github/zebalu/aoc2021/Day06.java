package io.github.zebalu.aoc2021;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day06 {
    private static final Map<FishStep, Long> CACHE = new HashMap<>();

    public static void main(String[] args) {
        var fishes = Arrays.stream(INPUT.split(",")).map(Integer::parseInt).toList();
        firstPart(fishes);
        secondPart(fishes);
    }

    private static void firstPart(List<Integer> fishes) {
        System.out.println(fishes.stream().mapToLong(fish -> repoductionsOf(fish, 80, CACHE)).sum());
    }

    private static void secondPart(List<Integer> fishes) {
        System.out.println(fishes.stream().mapToLong(fish -> repoductionsOf(fish, 256, CACHE)).sum());
    }

    private static long repoductionsOf(int fish, int steps, Map<FishStep, Long> cache) {
        FishStep fs = new FishStep(fish, steps);
        if (cache.containsKey(fs)) {
            return cache.get(fs);
        }
        long sum = 1L;
        int remainingSteps = steps - fish;
        while (remainingSteps > 0) {
            sum += repoductionsOf(9, remainingSteps, cache);
            remainingSteps -= 7;
        }
        cache.put(fs, sum);
        return sum;
    }

    private static final record FishStep(int fish, int steps) {

    }

    private static final String INPUT = """
            5,1,1,1,3,5,1,1,1,1,5,3,1,1,3,1,1,1,4,1,1,1,1,1,2,4,3,4,1,5,3,4,1,1,5,1,2,1,1,2,1,1,2,1,1,4,2,3,2,1,4,1,1,4,2,1,4,5,5,1,1,1,1,1,2,1,1,1,2,1,5,5,1,1,4,4,5,1,1,1,3,1,5,1,2,1,5,1,4,1,3,2,4,2,1,1,4,1,1,1,1,4,1,1,1,1,1,3,5,4,1,1,3,1,1,1,2,1,1,1,1,5,1,1,1,4,1,4,1,1,1,1,1,2,1,1,5,1,2,1,1,2,1,1,2,4,1,1,5,1,3,4,1,2,4,1,1,1,1,1,4,1,1,4,2,2,1,5,1,4,1,1,5,1,1,5,5,1,1,1,1,1,5,2,1,3,3,1,1,1,3,2,4,5,1,2,1,5,1,4,1,5,1,1,1,1,1,1,4,3,1,1,3,3,1,4,5,1,1,4,1,4,3,4,1,1,1,2,2,1,2,5,1,1,3,5,2,1,1,1,1,1,1,1,4,4,1,5,4,1,1,1,1,1,2,1,2,1,5,1,1,3,1,1,1,1,1,1,1,1,1,1,2,1,3,1,5,3,3,1,1,2,4,4,1,1,2,1,1,3,1,1,1,1,2,3,4,1,1,2""";

}
