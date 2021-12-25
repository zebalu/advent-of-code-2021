package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

public class Day24 {
    static int min = Integer.MAX_VALUE;
    static long c = 0;
    static final long tooLow = 89961192699411L;
    public static void main(String[] args) {
        var instructions = INPUT.lines().map(Instruction::read).toList();
        List<List<Instruction>> separated = new ArrayList<>();
        for (Instruction i : instructions) {
            if (i instanceof Inp) {
                separated.add(new ArrayList<>());
            } else {
                separated.get(separated.size() - 1).add(i);
            }
        }
        int dMax = 26*26*26*26;
        Map<Integer, Set<Integer>> prev = new HashMap<>();
        prev.put(0, new HashSet<>());
        for(int n= 1; n<=9; ++n) {
           Computer c = new Computer(0L);
           c.registers.put("w", n);
           c.execute(separated.get(0));
           prev.get(0).add(c.registers.get("z"));
        }
        prev.put(13, new HashSet<>());
        for(int n= 1; n<=9; ++n) {
            for(int z=0; z<26; ++z) {
            Computer c = new Computer(0L);
            c.registers.put("w", n);
            c.execute(separated.get(0));
            prev.get(13).add(c.registers.get("z"));
            }
         }
//        for(var l:separated) {
//            if(prev.containsKey(separated.indexOf(l))) {
//                //
//            } else {
//            Set<Integer> set = new HashSet<>();
//            //for(int z=0; z<26*26*26*26; ++z) {
//            for(int  z : prev.get(separated.indexOf(l)-1)) {
//                for(int n=1; n<=9&&z<dMax; ++n) {
//                    Computer c = new Computer(0L);
//                    c.registers.put("w", n);
//                    c.registers.put("z", z);
//                    c.execute(l);
//                    set.add(c.registers.get("z"));
//                }
//            }
//            System.out.println(set.size());
//            prev.put(separated.indexOf(l), set);
//            }
//        }
//        long prod = 1L;
//        for(int i=0; i<14; ++i) {
//            prod*=prev.get(i).size();
//            System.out.println("i: "+i+"\t"+prev.get(i).size());
//        }
//        System.out.println(prod);
//        for(var l: separated) {
//            System.out.println(separated.indexOf(l)+"\tisDown:"+isDown(l));
//            System.out.println("inp w");
//            l.forEach(System.out::println);
//            System.out.println("\n\n");
//        }
        System.out.println("found: "+couldYieldZ(instructions, separated, 13, 0, true, ""));
//        StringBuilder sb = new StringBuilder();
//        /*
//        sb.append(couldYieldZ(instructions, separated, 13, 0, false, ""));
//        System.out.println(sb.reverse());
//        System.out.println(min);
//        Computer c = new Computer(89961192699411L);
//        System.out.println(c.execute(instructions));
//        System.out.println(c);
//        */
//        long number = 99_999_999_999_999L;
//        boolean found = true;
//        long steps = 0L;
//        while (!found) {
//            ++steps;
//            found = new Computer(number).execute(instructions);
//            if (found) {
//                System.out.println(number);
//            }
//            number = decrement(number);
//            if (steps % 1000 == 0) {
//                System.out.println("steps: " + steps + "\tnum: " + number);
//            }
//        }
//        int pow = getPow26(1);
//        System.out.println(minInPow26(pow));
//        System.out.println(maxInPow26(pow));
//        pow=getPow26(27);
//        System.out.println(minInPow26(pow));
//        System.out.println(maxInPow26(pow));

        System.out.println("end :(");
    }

    static List<Integer> couldYieldZero(List<Instruction> instructions) {
        List<Integer> result = new ArrayList<>();
        List<Integer> fallback = new ArrayList<>();
        for (int i = 1; i <= 9; ++i) {
            fallback.add(i);
            for(int z=0; z<25; ++z) {
                Computer computer = new Computer(0);
                computer.registers.put("w", i);
                computer.registers.put("z", z);
                if (computer.execute(instructions)) {
                    result.add(i);
                }
            }
        }
        if (result.isEmpty()) {
            return fallback;
        }
        return result.stream().distinct().toList();
    }
    
    static String couldYieldZ(List<Instruction> full, List<List<Instruction>> instructions, int level, int neededZ, boolean forward, String sofar) {
        if(level<min) {
            System.out.println("new min: "+level);
            min=level;
        }
        
        int i = forward?1:9;
        int stop = forward?10:0;
        int diff = forward?1:-1;
        int pow = getPow26(neededZ);
        int next = 0;
        int min = minInPow26(pow-1);
        Function<Integer, Integer> zDif = x->x+1;
        if (isDown(instructions.get(level))) {
            next = (neededZ+1)*26;//maxInPow26(pow)*26;
            min = neededZ; //minInPow26(pow);
        } else {
            next =neededZ; //maxInPow26(pow); // maxInPow26(pow);
            min = neededZ/26;// minInPow26(pow-1); //minInPow26(pow); //minInPow26(pow)-1;
            zDif = x->x+1; //+26;
            
        }
        ++c;
        if(c%1L==0L) {
            System.out.println(c+"\tlevel: "+level+"\tneededZ: "+neededZ+"\tmin: "+min+"\tmax: "+next+"\t"+sofar);
        }
        for (; i != stop; i+=diff) {
            for(int z=min; z<next; z=zDif.apply(z)) {
                Computer computer = new Computer(0);
                computer.registers.put("w", i);
                computer.registers.put("z", z);
                if(computer.execute(instructions.get(level)) && computer.registers.get("z")==neededZ) {
                    if(level>0) {
                        
                        if(computer.registers.get("z")!=neededZ) {
                            System.out.println(computer);
                            System.out.println("level: "+level+"\t neededZ: "+neededZ+"\t"+computer.registers.get("z"));
                            System.exit(1);
                        }
                        
                        var previous = couldYieldZ(full, instructions, level-1, z, forward, i+sofar);
                        //System.out.println("level: "+level+"\tnum: "+i+"\tmod: "+z+"\tlup: "+previous+"\tchecked: "+(i+sofar));
                        if(!previous.isEmpty()) {
                            return previous;
                        }
                    } else {
                        Computer checker = new Computer(Long.valueOf(i+sofar));
                        System.out.println(i+sofar);
                        if(checker.execute(full)) { // && checker.registers.get("z") == 0) {
                            return i+sofar;
                        }
                    }
                }
            }
        }
        return "";
    }
    
    private static int getPow26(int num) {
        int current = num;
        int count = 0;
        while(current>0) {
            ++count;
            current/=26;
        }
        return count;
    }
    
    private static int minInPow26(int pow) {
        int min = 1;
        for(int i=1; i<pow; ++i) {
            min*=26;
        }
        return min-1;
    }
    
    private static int maxInPow26(int pow) {
        int max = 1;
        for(int i=1; i<=pow; ++i) {
            max*=26;
        }
        return max;
    }
    
    static boolean isDown(List<Instruction> instructions) {
        return instructions.stream().filter(i->i instanceof Div).map(Instruction::toString).anyMatch(s->s.equals("div z 26"));
    }

    static long decrement(long number) {
        var candidate = number - 1;
        String asString = Long.toString(candidate);
        if (asString.length() != 14) {
            throw new IllegalArgumentException("can not decrement: " + number);
        }
        if (asString.contains("0")) {
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            for (int i = 0; i < asString.length(); ++i) {
                if (found) {
                    sb.append("0");
                } else {
                    char c = asString.charAt(i);
                    found = c == '0';
                    sb.append(c);
                }
            }
            return decrement(Long.parseLong(sb.toString()));
        }
        return candidate;
    }

    static long increment(long number) {
        var candidate = number + 1;
        String asString = Long.toString(candidate);
        if (asString.length() != 14) {
            throw new IllegalArgumentException("can not decrement: " + number);
        }
        if (asString.contains("0")) {
            StringBuilder sb = new StringBuilder();
            boolean found = false;
            for (int i = 0; i < asString.length(); ++i) {
                if (found) {
                    sb.append("1");
                } else {
                    char c = asString.charAt(i);
                    found = c == '0';
                    if (found) {
                        sb.append('1');
                    } else {
                        sb.append(c);
                    }
                }
            }
            return Long.parseLong(sb.toString());
        }
        return candidate;
    }

    private static class Computer {
        int[] digits;
        int inputPointer = 0;
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
                instructions.forEach(i -> {
//                    String c = toString();
//                    System.out.print(i+"\t"+c);
                    i.execute(this);
//                    System.out.println(" --> "+this);
                });
                return true;//registers.get("z") == 0;
            } catch (Exception e) {
                //e.printStackTrace();
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
            s="inp "+register;
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
            s="mul "+register+" "+mul;
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
            s="add "+register+" "+add;
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
            s="mod "+register+" "+mod;
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
            s="div "+register+" "+div;
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
            s="eql "+register+" "+eql;
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
