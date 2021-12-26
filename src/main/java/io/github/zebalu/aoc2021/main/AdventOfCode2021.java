package io.github.zebalu.aoc2021.main;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.function.Consumer;

import io.github.zebalu.aoc2021.Day01;
import io.github.zebalu.aoc2021.Day02;
import io.github.zebalu.aoc2021.Day03;
import io.github.zebalu.aoc2021.*;
import io.github.zebalu.aoc2021.Day05;
import io.github.zebalu.aoc2021.Day06;
import io.github.zebalu.aoc2021.Day07;
import io.github.zebalu.aoc2021.Day08;
import io.github.zebalu.aoc2021.Day09;

public class AdventOfCode2021 {

    public static void main(String[] args) {
        var days = new ArrayList<Day>(25);
        days.add(new Day(1, Day01::main, "Sonar Sweep"));
        days.add(new Day(2, Day02::main, "Dive!"));
        days.add(new Day(3, Day03::main, "Binary Diagnostic"));
        days.add(new Day(4, Day04::main, "Giant Squid"));
        days.add(new Day(5, Day05::main, "Hydrothermal Venture"));
        days.add(new Day(6, Day06::main, "Lanternfish"));
        days.add(new Day(7, Day07::main, "The Treachery of Whales"));
        days.add(new Day(8, Day08::main, "Seven Segment Search"));
        days.add(new Day(9, Day09::main, "Smoke Basin"));
        days.add(new Day(10, Day10::main, "Syntax Scoring"));
        days.add(new Day(11, Day11::main, "Dumbo Octopus"));
        days.add(new Day(12, Day12::main, "Passage Pathing"));
        days.add(new Day(13, Day13::main, "Transparent Origami"));
        days.add(new Day(14, Day14::main, "Extended Polymerization"));
        days.add(new Day(15, Day15::main, "Chiton"));
        days.add(new Day(16, Day16::main, "Packet Decoder"));
        days.add(new Day(17, Day17::main, "Trick Shot"));
        days.add(new Day(18, Day18::main, "Snailfish"));
        days.add(new Day(19, Day19::main, "Beacon Scanner"));
        days.add(new Day(20, Day20::main, "Trench Map"));
        days.add(new Day(21, Day21::main, "Dirac Dice"));
        days.add(new Day(22, Day22::main, "Reactor Reboot"));
        days.add(new Day(23, Day23::main, "Amphipod"));
        days.add(new Day(24, Day24::main, "Arithmetic Logic Unit"));
        days.add(new Day(25, Day25::main, "Sea Cucumber"));
        Instant start = Instant.now();
        days.forEach(Day::execute);
        System.out.println();
        System.out.println("the whole advent took: "+Duration.between(start, Instant.now()).toSeconds()+" seconds");
        System.out.println("Merry Christmas to you!");
    }
    
    private static record Day(int number, Consumer<String[]> main, String title) {
        void execute() {
            System.out.println("################################################################################");
            System.out.println(String.format("   --- Day %02d: %s ---   ", number, title));
            Instant start = Instant.now();
            main.accept(null);
            System.out.println("took: "+Duration.between(start, Instant.now()).toMillis()+" ms");
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        }
    }

}
