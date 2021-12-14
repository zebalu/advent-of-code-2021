package io.github.zebalu.aoc2021;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Day14 {
    public static void main(String[] args) {
        String[] parts = INPUT.split("\n\n");
        String template = parts[0];
        var rules = parts[1].lines().map(l -> l.split(" -> ")).collect(Collectors.toMap(ps -> ps[0], ps -> ps[1]));
        firstPart(template, rules);
        secondPart(template, rules);
    }

    private static void firstPart(String template, Map<String, String> rules) {
        replaceNTimes(10, template, rules);
    }

    private static void secondPart(String template, Map<String, String> rules) {
        replaceNTimes(40, template, rules);
    }
    
    private static void replaceNTimes(int n, String template, Map<String, String> rules) {
        Map<String, Long> pairCount = initialPairCount(template);
        for (int i = 0; i < n; ++i) {
            pairCount = stepPairsByRules(pairCount, rules);
        }
        var normalized = normalizedCharCount(pairCount, template);
        long min = normalized.values().stream().mapToLong(Long::longValue).min().orElseThrow();
        long max = normalized.values().stream().mapToLong(Long::longValue).max().orElseThrow();
        System.out.println(max - min);
    }

    private static Map<String, Long> stepPairsByRules(Map<String, Long> pairCount, Map<String, String> rules) {
        Map<String, Long> currentPairCount = new HashMap<>();
        for (String key : pairCount.keySet()) {
            String val = rules.get(key);
            String replaced1 = key.substring(0, 1) + val;
            String replaced2 = val + key.substring(1, 2);
            long count = pairCount.getOrDefault(key, 0L);
            currentPairCount.compute(replaced1, (k, v) -> v == null ? count : v + count);
            currentPairCount.compute(replaced2, (k, v) -> v == null ? count : v + count);
        }
        return currentPairCount;
    }

    private static Map<Character, Long> normalizedCharCount(Map<String, Long> pairCount, String template) {
        Map<Character, Long> charCount = new HashMap<>();
        charCount.compute(template.charAt(0), (k,v)->v==null?1L:v+1L);
        charCount.compute(template.charAt(template.length()-1), (k,v)->v==null?1L:v+1L);
        pairCount.entrySet().stream().forEach(e -> {
            charCount.compute(e.getKey().charAt(0), (k, v) -> v == null ? e.getValue() : v + e.getValue());
            charCount.compute(e.getKey().charAt(1), (k, v) -> v == null ? e.getValue() : v + e.getValue());
        });
        var normalized = charCount.entrySet().stream().map(e -> Map.entry(e.getKey(), e.getValue() / 2))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return normalized;
    }

    private static Map<String, Long> initialPairCount(String template) {
        Map<String, Long> pairCount = new HashMap<>();
        for (int i = 0; i < template.length() - 1; ++i) {
            pairCount.compute(template.substring(i, i + 2), (k, v) -> v == null ? 1L : v + 1L);
        }
        return pairCount;
    }
    
    private static final String INPUT = """
            NBOKHVHOSVKSSBSVVBCS

            SN -> H
            KP -> O
            CP -> V
            FN -> P
            FV -> S
            HO -> S
            NS -> N
            OP -> C
            HC -> S
            NP -> B
            CF -> V
            NN -> O
            OS -> F
            VO -> V
            HK -> N
            SV -> V
            VC -> V
            PH -> K
            NH -> O
            SB -> N
            KS -> V
            CB -> H
            SS -> P
            SP -> H
            VN -> K
            VP -> O
            SK -> V
            VF -> C
            VV -> B
            SF -> K
            HH -> K
            PV -> V
            SO -> H
            NK -> P
            NO -> C
            ON -> S
            PB -> K
            VS -> H
            SC -> P
            HS -> P
            BS -> P
            CS -> P
            VB -> V
            BP -> K
            FH -> O
            OF -> F
            HF -> F
            FS -> C
            BN -> O
            NC -> F
            FC -> B
            CV -> V
            HN -> C
            KF -> K
            OO -> P
            CC -> S
            FF -> C
            BC -> P
            PP -> F
            KO -> V
            PC -> B
            HB -> H
            OB -> N
            OV -> S
            KH -> B
            BO -> B
            HV -> P
            BV -> K
            PS -> F
            CH -> C
            SH -> H
            OK -> V
            NB -> K
            BF -> S
            CO -> O
            NV -> H
            FB -> K
            FO -> C
            CK -> P
            BH -> B
            OH -> F
            KB -> N
            OC -> K
            KK -> O
            CN -> H
            FP -> K
            VH -> K
            VK -> P
            HP -> S
            FK -> F
            BK -> H
            KV -> V
            BB -> O
            KC -> F
            KN -> C
            PO -> P
            NF -> P
            PN -> S
            PF -> S
            PK -> O""";
}
