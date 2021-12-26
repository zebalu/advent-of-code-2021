package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

public class Day24 {

    public static void main(String[] args) {
        var separated = getSeparatedInstructions();
        firstPart(separated);
        secondPart(separated);
    }

    private static void firstPart(List<List<Instruction>> separated) {
        var minMax = withInstructions(separated);
        System.out.println(minMax.max());
    }

    private static void secondPart(List<List<Instruction>> separated) {
        var minMax = withInstructions(separated);
        System.out.println(minMax.min());
    }

    private static List<List<Instruction>> getSeparatedInstructions() {
        var instructions = INPUT.lines().map(Instruction::read).toList();
        List<List<Instruction>> separated = new ArrayList<>();
        for (Instruction i : instructions) {
            if (i instanceof Inp) {
                separated.add(new ArrayList<>());
            } else {
                separated.get(separated.size() - 1).add(i);
            }
        }
        return separated;
    }

    private static MinMax withInstructions(List<List<Instruction>> instructions) {
        int currPow = 0;
        var validZs = new HashMap<Integer, MinMax>();
        validZs.put(0, new MinMax(0L, 0L));
        for (var digitChecker : instructions) {
            currPow += isDown(digitChecker) ? -1 : +1;
            var minZ = Math.pow(26, currPow - 1) - 1;
            var maxZ = Math.pow(26, currPow);
            var currentZs = new HashMap<Integer, MinMax>();
            for (var zMinMaxEntry : validZs.entrySet()) {
                var z = zMinMaxEntry.getKey();
                var currMinMax = zMinMaxEntry.getValue();
                for (int digit = 1; digit <= 9; ++digit) {
                    var w = digit;
                    var newZ = calculateNewZ(digitChecker, z, digit);
                    if (minZ < newZ && newZ < maxZ) {
                        currentZs.compute(newZ,
                                (k, v) -> v == null ? new MinMax(currMinMax.min() * 10 + w, currMinMax.max() * 10 + w)
                                        : v.bestOf(currMinMax.min() * 10 + w, currMinMax.max() * 10 + w));
                    }
                }
            }
            validZs = currentZs;
        }
        return validZs.get(0);
    }

    private static int calculateNewZ(List<Instruction> instructions, Integer incommingZ, int digit) {
        Computer c = new Computer(0L);
        c.registers.put("w", digit);
        c.registers.put("z", incommingZ);
        c.execute(instructions);
        return c.registers.get("z");
    }

    private static record MinMax(long min, long max) {
        MinMax bestOf(long min, long max) {
            return new MinMax(Math.min(this.min, min), Math.max(this.max, max));
        }
    }

    private static boolean isDown(List<Instruction> instructions) {
        return instructions.stream().filter(i -> i instanceof Div).map(Instruction::toString)
                .anyMatch(s -> s.equals("div z 26"));
    }

    private static class Computer {
        private int[] digits;
        private int inputPointer = 0;
        Map<String, Integer> registers = new HashMap<>();

        Computer(long digitLong) {
            digits = toDigits(digitLong);
            registers.put("w", 0);
            registers.put("x", 0);
            registers.put("y", 0);
            registers.put("z", 0);
        }

        int getNumber(String s) {
            if (registers.containsKey(s)) {
                return registers.get(s);
            }
            return Integer.parseInt(s);
        }

        boolean execute(List<Instruction> instructions) {
            try {
                instructions.forEach(i -> i.execute(this));
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private int[] toDigits(long num) {
            Stack<Integer> stack = new Stack<>();
            var n = num;
            while (n > 0) {
                stack.push((int) (n % 10));
                n = n / 10;
            }
            int[] res = new int[stack.size()];
            int i = 0;
            while (!stack.isEmpty()) {
                res[i++] = stack.pop();
            }
            return res;
        }

        @Override
        public String toString() {
            return registers.toString();
        }

    }

    private static abstract sealed class Instruction permits Inp,Mul,Add,Mod,Div,Eql {
        abstract void execute(Computer computer);

        static Instruction read(String line) {
            var parts = line.split(" ");
            return switch (parts[0]) {
            case "inp" -> new Inp(parts[1]);
            case "mul" -> new Mul(parts[1], parts[2]);
            case "add" -> new Add(parts[1], parts[2]);
            case "mod" -> new Mod(parts[1], parts[2]);
            case "div" -> new Div(parts[1], parts[2]);
            case "eql" -> new Eql(parts[1], parts[2]);
            default -> throw new IllegalArgumentException("Unknown instruction: '" + parts[0] + "' in line: " + line);
            };
        }
    }

    private static final class Inp extends Instruction {
        private final String register;
        private final String s;

        Inp(String register) {
            this.register = register;
            s = "inp " + register;
        }

        @Override
        void execute(Computer computer) {
            computer.registers.put(register, computer.digits[computer.inputPointer++]);
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class Mul extends Instruction {
        private final String register;
        private final Function<Computer, Integer> mulNum;
        private final String s;

        Mul(String register, String mul) {
            this.register = register;
            this.mulNum = c -> c.getNumber(mul);
            s = "mul " + register + " " + mul;
        }

        @Override
        void execute(Computer computer) {
            computer.registers.put(register, computer.registers.get(register) * mulNum.apply(computer));
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class Add extends Instruction {
        private final String register;
        private final Function<Computer, Integer> addNum;
        private final String s;

        Add(String register, String add) {
            this.register = register;
            this.addNum = c -> c.getNumber(add);
            s = "add " + register + " " + add;
        }

        @Override
        void execute(Computer computer) {
            computer.registers.put(register, computer.registers.get(register) + addNum.apply(computer));
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class Mod extends Instruction {
        private final String register;
        private final Function<Computer, Integer> modNum;
        private final String s;

        Mod(String register, String mod) {
            this.register = register;
            this.modNum = c -> c.getNumber(mod);
            s = "mod " + register + " " + mod;
        }

        @Override
        void execute(Computer computer) {
            int num = computer.registers.get(register);
            int mod = modNum.apply(computer);
            if (num < 0 || mod <= 0) {
                throw new IllegalStateException("illegal numbers: " + num + " or " + mod + " is <= 0");
            }
            computer.registers.put(register, computer.registers.get(register) % modNum.apply(computer));
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class Div extends Instruction {
        private final String register;
        private final Function<Computer, Integer> divNum;
        private final String s;

        Div(String register, String div) {
            this.register = register;
            this.divNum = c -> c.getNumber(div);
            s = "div " + register + " " + div;
        }

        @Override
        void execute(Computer computer) {
            int divisor = divNum.apply(computer);
            if (divisor == 0) {
                throw new IllegalStateException("can not divide with 0");
            }
            computer.registers.put(register, computer.registers.get(register) / divNum.apply(computer));
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class Eql extends Instruction {
        private final String register;
        private final Function<Computer, Integer> eqlNum;
        private final String s;

        Eql(String register, String eql) {
            this.register = register;
            this.eqlNum = c -> c.getNumber(eql);
            s = "eql " + register + " " + eql;
        }

        @Override
        void execute(Computer computer) {
            computer.registers.put(register, computer.registers.get(register).equals(eqlNum.apply(computer)) ? 1 : 0);
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final String INPUT = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 13
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 14
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 12
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 8
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 11
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 5
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x 0
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 4
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 15
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 10
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -13
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 13
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 10
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 16
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -9
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 5
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 11
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 6
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 13
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 13
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -14
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 6
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -3
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 7
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -2
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 13
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -14
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 3
            mul y x
            add z y""";
}
