package io.github.zebalu.aoc2021;

import java.util.Set;
import java.util.Stack;

public class Day10 {

    public static void main(String[] args) {
        firstPart();
        secondPart();
    }

    private static void firstPart() {
        int sum = INPUT.lines().mapToInt(Day10::corruptLinePoint).sum();
        System.out.println(sum);
    }

    private static void secondPart() {
        var scores = INPUT.lines().filter(l -> corruptLinePoint(l) == 0).mapToLong(Day10::lineClosingPoint).sorted()
                .toArray();
        System.out.println(scores[scores.length / 2]);
    }

    private static int corruptLinePoint(String line) {
        Stack<Chunk> chunks = new Stack<>();
        for (int i = 0; i < line.length(); ++i) {
            char ch = line.charAt(i);
            if (Chunk.isStartChar(ch)) {
                chunks.push(Chunk.forStartChar(ch));
            } else if (chunks.isEmpty() || chunks.peek().endChar != ch) {
                return Chunk.missingCharPoint(ch);
            } else {
                chunks.pop();
            }
        }
        return 0;
    }

    private static long lineClosingPoint(String line) {
        Stack<Chunk> chunks = new Stack<>();
        for (int i = 0; i < line.length(); ++i) {
            char ch = line.charAt(i);
            if (Chunk.isStartChar(ch)) {
                chunks.push(Chunk.forStartChar(ch));
            } else if (chunks.isEmpty() || chunks.peek().endChar != ch) {
                throw new IllegalArgumentException("This method can not get corrupt lines! '"+line+"'");
            } else {
                chunks.pop();
            }
        }
        long sum = 0L;
        while (!chunks.isEmpty()) {
            sum *= 5L;
            sum += chunks.pop().closingValue;
        }
        return sum;
    }

    private static enum Chunk {
        C1('(', ')', 3, 1), C2('[', ']', 57, 2), C3('{', '}', 1197, 3), C4('<', '>', 25137, 4);

        static final Set<Character> START_CHARS = Set.of(C1.startChar, C2.startChar, C3.startChar, C4.startChar);
        private final char startChar;
        private final char endChar;
        private final int missingValue;
        private final int closingValue;

        private Chunk(char startChar, char endChar, int missingValue, int closingValue) {
            this.startChar = startChar;
            this.endChar = endChar;
            this.missingValue = missingValue;
            this.closingValue = closingValue;
        }

        static boolean isStartChar(char ch) {
            return START_CHARS.contains(ch);
        }

        static Chunk forStartChar(char ch) {
            return switch (ch) {
            case '(' -> C1;
            case '[' -> C2;
            case '{' -> C3;
            case '<' -> C4;
            default -> throw new IllegalArgumentException("Unexpected value: " + ch);
            };
        }

        static int missingCharPoint(char ch) {
            for (Chunk c : values()) {
                if (c.endChar == ch) {
                    return c.missingValue;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + ch);
        }
    }

    private static final String INPUT = """
            [[{[<{{<<{<[<{<>[]}([][])>{{<>()}<()>}](({{}<>}[[]<>])<([]<>){[]()}>)>[<{{<>}<[]()>}[{[][]}<{}{}>]><{<
            {[<[({([[(<<(([]{}))<[{}()]>>>)]])<([<{[{({}{})[[]()]}[(()[])<{}()]]]{{[[][]]<<>[]>}}}{([([
            {<{<{(({[[([{<<><>>([]())}[{()[]}({}())]}<{{{}}{[]<>}}({<>{}})>){<(([]())([]<>))(<<>[]><[]<>>)>({[[]
            {[([<{<([{{(({()<>}({}<>)))(<{<>{}}](<<>[]><()()>))}{[{{[]()}(<>[])}{{[][]}{<>{}}}]}}{[{{{{}[]}<<>()>}}<{([]
            [({[{[(<<<[((<{}<>><{}{}>)({[]<>}((){}))){{[{}<>]{{}<>}}})[<[<<>>[<>{}]]{{<>[]}}>]>>[[([<{<><>}
            <[{[{[[<{[[{<<[]()>[{}<>]>}]<([{{}}({}{})])>]{<[[<<><>><()[]>]([[]<>]{[]<>})]{(([][]){()[]})
            ((<(<(<[(<([(<{}<>>(()()))](<{{}[]}{()[]}>))<<<(<>[]){()()}>([<>{}]{{}})>({{[][]}(()())}(<
            {{([(<{<([([[[<><>][()<>]]{(()[])}])]({<[<<>>[()<>]]({()[]}[{}()])>]))>({<<[<{[]()}<<>[]>>{<[]
            ([<{{((<({<[<({}<>)[[]()]>]([(<>)<(){}>](([][]){{}{}}))>{(({<>[]}<()()>){(()[])})[{{[][]}((){})}[([]<>)({}{})
            <<<{<{<([<[((<()<>]<[]()>)[(<>[])[{}<>]])]><[<[(()())[()()]]{[{}{}][()()]}>]{<<(<>())[<>[]]>>[<(()())<[]
            {<[[({<[{[{[({{}<>}<<>{}>){<{}[]><{}{}>}]{{<<>>}<(<>()){()<>}>}}]}{{<[({{}()})([()<>]{[]})
            (((([[(((<(<<(<>())[(){}]>([[][]]{()()})>{<[<>[]][()<>]>(([]{})[<>{}])})[({{()<>}({}())})]>([[[(()<>)][[<>()
            [<({([[[<<([<<<>()>>{[<>()](<>{})}]){<[[{}<>]]<{{}<>}([]{})>>}>[<<<{{}()}>>[[([][])<<><>>]({<>
            {<(<<{{<<[({{({}<>)[[][]]}(<()[]>[<>])}<{<()>{{}()}}>){{<[(){}][(){}>><{{}<>}[[]{}]>}}]<{<{
            {(<{<[<(<<(<[([][])<[]{}>]><{([]())(<>{})}{<(){}><[][]>}>><({({}[]){<>()}}{<{}>(())})>>{({{(<><>)[{}()]}
            [(<{[<<[{((({[{}<>][[]<>]}){(<{}<>>({}<>))<(<><>)[<>]>})<({{<>[]}}}>)}]>([{{<<{{[][]}(<>())}<{{}
            <<<[[{[<<{[{<(()[])<[]()>>}[(({}[]))(({}<>){<>})]](([{(){}}[<>]]<[{}{}]<{}<>>>))}>{{[(((()[]){(){}})(<(){}>{
            [{{<<([[[[<<[{(){}}{(){}}]{<{}()>(()())}>{{<{}()>}<[<><>][()()]>)>([{{()<>}<()[]>}[[<><>]{[]<>}]]{[{
            <[[{[{<[(({({[<>{}]}([<>[]]))<<[<><>]([]<>)>([{}()][()()))>}[((<{}>{[]{}}))[[{{}}[<><>]]]])(([{{[]()}<[]
            {{{<{([[[<[<{{(){}}({}{})}>[[{()[]}<[]{}>]<<()<>>[()[]]>]}<{[[[]()]<<><>>]({<><>}<[][]>)}{(<[]()><{}<>>)}>>
            ([<<[(([{{[([{<>{}}])<[(()){<>{}}]>](<{([][])[<>()]}<{{}()}>>)}>{[{{(<()()>[<><>])((<><>){()()})}{([[][]])[[{
            [{((({<([{(<{(<>)[[]()]}({{}<>}<[]<>>)>)}(<<[({}{})]{({}<>)[<>()]}>>[<[[()<>]{<>()}][<[][]><<>{}>]>[<([][])[(
            (<[{<[<((<{<[{<>[]}{{}<>}]([[][]]{{}[]})>[<[(){}]{[]()}>({<>}{<><>}}]}>[[(<{()[]}<()[]>>[{{}()}<<>[]>
            ({{<([{<<<<<{{()}({}())}<[()]{[]{}}>>[([()<>]([][]))<{()<>}>]>>>>}<{[[[<<<<>()>{[]{}}>[({}()){(){}}]
            <[{([<<(<(<[(({}()){[][]})[[(){}][[]()]]][<[{}]([][])>]>}>(<{[({[]<>}{(){}})<(<>)({}())>]<[({}<>)<{
            (({[{<({<<[<{{[]<>}[<><>]}{{{}}<()[]>}>]<<<{()()}{{}<>}>([[]()][<>()])>>><[([(<><>)<()[]>]
            {([<<[{[(({[{{()<>}({}{})}<[{}[]]<(){}>>]<{<[]<>>}<[()()][()()]>>}<<[[(){}](<><>)]><(<{}[]>
            {([<{<([(<(<(<[]{}>{<>()}){{<>{}}}>({<[][]>(<>{})}))<[<[{}{}]<{}{}>>{(<>[])(<>())}][<{{}<>}(()[]
            [{[(<(({{(<{(<[][]>){[<>{}]}}{<{(){}}{<><>}>({[]{}}{[]{}})}>)(<[(([]<>))[[[]()]<[]<>>]]<{({}[])<{}<>>}[
            [{<{({((<{[{[<<>{}>[[]]]{<{}()>[<>]}>]}>[{([(<[][]>{[]()}){<(){}>[<>[]]}][[([]<>)<[]{}>]{{{}}[{}]}])([
            {<(<({[{[(({((<>[])<{}{}>)({[]<>>[{}[]])}([{(){}}([])]{<<>[]>[{}()]}))[[<[<><>]<<><>>>{[()()]<
            {{{<[<<{(<(([({}<>)<()>]{<()()><[]<>>})(<[()[]]{[]{}}>{{{}{}}(<>())}))({([[]()]{()[]}){{{}<>}[
            (<({{[{<{[<[[{[]()}]([[]()][<>{}])][[[<>]{<>}]<([]{})<{}[]>>]>{[{{<><>}[[][]]}<{[]{}}({}())>](<[[]()]({
            <<[{[<{<[[(<[<(){}>(<>)](<[]<>>[[]()])>{<<{}[]><{}()>>[(()[])(<>{})]})({({{}<>}[<>{}])}{({[][]}<<><>>)})]({<[
            [[([<{{(<({[{([][])(()<>)}]{{{[]<>}[()()]}[({}{})[()[]]]}}>><[[{{[()()]({}())}}({(()[])[[]()]}{
            <<{(<(([{((<([()()]){{[]<>}{{}()}}>)<<<({}<>)[<>{}]><({}[])<[]<>>>>(<[[]]{(){}}>(([][])([])))>)[{[{([]<>)[<>{
            [(<(<<{{[([[[({}{})<()())]({[]}{{}[]})]({[()()]([]{})}{({}[])[[]()]})]<[[{<><>}<[][]>][[[]()]{(){}}]]>){(([<(
            [[<[<{<[{({{{{<>{}}<[]()>}<<(){}>>}<{{{}{}}{()<>}}[([]{}){<>{}})>})[[((<<><>>[{}[]])<{()<>}<()
            ((<(<<[[{{{(<<{}()>[()()]><((){})>)[[[[][]][()[]]][{[]<>}<{}{}>]])((([[]{}]<[]{}>))([{<>{}}(<
            {{(({[{[({({<<<>[]><<>[]>>{{()[]}[{}()]}})[{(<{}<>><[]>)(({}[]){{}[]})}<<<[]()><[]{}>><{<><>}<()<>>>>]})
            [(<[{[<({{[{<[<>()]([]{})><{(){})[()[]]>}<(<()()>(()()))(<{}[]>({}[]))>]([([[]()]({}{}))({(){}}([](
            {({(({[<{<[<<{<><>}(<>[])>[<{}{}><[]<>>]><<{<>}>>](<<<()<>>(()<>)>{<{}[]>([]<>)>>)>}<{<{{<<
            <([[[(({[{<(({<>[]}<<>[]>){<{}[]>([]()))){<({}<>)<[][]>>{<{}{}>{()[]}}}>{((({}{})[{}[]]))({[<><>]}<<<>{}
            [{[[[{<<(({[({[][]}<[]<>>)<{{}()}{{}}>]{({[]}[<>{}]){<[][]>[{}{}]}}})[[([<<>()>(<>[])]<<<>
            <({<<[(({[([{[()[]]({}{})}{{()[]}[()[]]}])[([((){}){<><>}]{<()[]>{{}{}}})]][[{<[()()]({}<>
            [{<([<({{<<[((<>[])(<><>))((()[])([][]))][(([][])<<>()>)]>[<<{()<>}<{}<>>><<{}<>>{()<>}]><{[()[
            [<{[<([{((({<<[]{}><{}{}>>([{}<>]<<><>>)})[[(<{}<>>([]<>))<<{}()>{{}<>}>]{[<[]<>>][[{}<>]<{}[
            ({<{<[<({([<{<()()>{{}[]}}({<>{}})><{[[]<>]<[]()>}<[{}()]{{}[]}>>])}([<<[{[]<>}<<><>>][<[]{
            {[({<[[<<<(<{<{}<>>(()())}[{()}{()<>}]>)[<{{(){}}(()<>)}>{[([]{})(()<>)]{[(){}]{()[]}}}]>{{[<<[]()><()<>>>{
            <[{{({({(<<{[(()<>){[]()}][<[]>(()())]}{<(<><>}{()[]}>}>(<<{{}()}<[][]>>[{()<>}{{}{}}]>)>)}<[(<((<(
            [<{[<[{[[[([<(<>{}){{}<>}>(<<>{}>{{}{}})]{({<>[]}<{}()>)<({}<>)<{}{}>>}){<[[[]{}]{{}{}}]<<[]<>>(()[])>
            {[{((<(<[(((<({}[]){[]()}>)))]([{<(<<>{}>[[]])<[[][]][(){}]>>[{[{}<>]{[]<>}}{(<>())(())}]}[{<{{}()}<<>[]>>}[[
            [[[{{(<<<<((<((){}){[]<>}><<(){}><()<>>>){[<[][]>({}[])](({}{})<[][]>)})[[{(<>>}([{}()][{}()])]]>>>((<{[
            [[({<[[<([<[((<>{})({}<>))({{}{}}(()[]))]<<((){})<()()>>[<[][]><{}{}>]>>({[<()()>[[][]]]})])>
            ([<(<{(<<[<{{([]<>)[<>[]]}<<()()>[(){}]>}><([[<>[]]](({}[])))({(()())<(){}>}<[<>>[()()]>)>]>>)
            {(({([{[{(<[[((){})(<>{})]<(()())(()[]>>]{([{}]{[][]})}>)<((((()<>)[<><>])<((){})[()()]>)[[<()<>>][<{}
            {{([{{<<({{<<<[]()><()[]>}{{()<>}}>[<{()<>}<[]{}>><{{}[]}[{}[]]>]}})({{({[<>{}]<(){}>}(<<>>(()<>)))(<
            <[[{<(<{{<[<{({}())}({[][]}{<><>})>{[<<>{}><()[]>]<[<>[]]<<>[]]>}]>(({[{[]()}<[]<>>]{<<>><<>[]>}}))}([[<
            ([{<[{<({{[((<{}<>>[(){}])({()<>})){{<<>{}>}[<{}()>{()[]}]}]}[{(({()[]}(<><>))[<{}()>(<>[])])<[{()[]}[
            {<[{({<((<({[{[][]}{{}[]}]{[<>()][<>{}]}}((({}())([]()))<([][])[[][]]>)}({[[[][]]({}())]}{({{}}){(<>[])
            {{[{{({(<(<[[[[]{}](<><>)]([[]{}]{<>[]})]>({{{()[]}([])}[{[][]}(<><>)]](({[]}<<>[]>))))>)}({{[{{(([])
            [<[{[{<<({{[{[<>{}]<(){}>}]}<{(({}()){{}<>})(<{}()>{[]<>})}{<[[]{}]>}>}[<[[([][])[[]]]]{{([]())[{}<>]}<({
            ([<({{[((<({(([]<>){[]<>))[({}())([]<>)]})[{{{{}<>}{{}[]}}}<<([]{})({}[])>[<()[]>]>]><([[[{}<>]<()>]([<>{}
            <[<{[[[<{{[[([[]<>]{(){}))[[{}<>][[][]]]](([<>[]]<<>[]>)[(()<>){()<>}])]}[<<((())(<>{}))<{[]()}<[
            [{[[{[[(<<(<({{}[])[<>()])><[[[]()](<>())]<{[]<>}({}())>>){[<{<>{}}[[]<>]>]}>{{<[<<>()>[()[]]
            [(([{(([{<[<[<{}[]>[<><>]]>({[{}{}]{[]{}}})]>}{{[[<[{}{})<()()>>{{<>}{{}[]}}]{{([][])[<>{}]}{
            (((<<<<[(<[<{{()()}<[]{}>}<{[][]}{<>[]}>>[[[()()]{{}{}}]]]({[<()()><[]{}>]{{[]{}}([]())}}<{<()<>>[<>]}<<{}
            <([<[[<<[(({(({}())<{}>)<(()[])[{}[]]>})<<({{}}[[]{}])[({}{})((){})]><<{{}()}{[][]}>>>)]>[[(
            <<<{(<{{(([{<{()[]}><<[]{}>{{}[]}>}{(<[][]>[[]<>])[{()<>}]}](<[[<><>][<>[]]]([()[]]<{}()>)>))<(
            {[[([<<[[(<((<()()>(<><>))[[{}()]<()<>>]){[((){}){[]{}}][[{}[]]]}>([<((){}){(){}}>[([]{}){()[]}]]))]<[<<{<<><
            ([{[{([<{{{[<(()[])({}())>{[<>{}]{{}<>}}]{(([]<>){<>}){[()<>]<()[]>}}}(<<[{}<>](()[])>{[[]()]
            [<((<<<[{[((([<>{}]){[(){}]})<<{<>{}}({}[])>[[[]{}]<()[]>]>){<(<<>()>(<>[]))[(()[]){[]<>}]>
            <(([({[[[[<<[[[]()]]<<()[]>>>>{{<[()[]]{{}{}}>{<[]()>{()[]}}}<[({}<>)[()<>]]>}]([([[()()]<<>{}>])<{<{
            <{([([((<{{[([()[]]{{}()})][(<<><>>){{{}[]}[[]<>]}]}}>[((<(<<>>[[]<>])[(()[]){()<>]]><<{{}
            {<[[({{<<[<<<{()[]}<<>{}>>([[]<>]((){}))>[{[<>()]{{}<>}}[({}())]]>{[{{[]()}({}())>]({{(){}}<()<>>}{<(){}>{
            {(<[{[{{[<(<({(){}}<[]()>)(<<>[]>{<>{}})>)>]({[{[({}()){<><>}]<[[]<>]{<>{}}>}(<<{}{}>([]<>)>(({}{}){(
            ((<((((<<({[{{{}[]}(()())}{<()()>}]<[[<>[]][(){}]][{()[]}]>}(([<[][]>][<[]()>({}[])])[<<[]()>[{}<>
            [[{<[(<{{<{<[{<>()}[[]]]({{}<>})>{{{()[]}<{}<>>}}}<({{()[]}}[[<>()](()[])])<[([]<>)[{}<>]][([][]
            <{{[{[{(<<({{{{}[]}{[]<>}}<[[]{}]{{}[]}>}[({{}{}}<[]()>){[[]<>]}])((<{()()}[[]{}]>({()<>}<
            ({{{<({{<<[<[{()<>}[[]<>]]<[{}{}][<>[]]>>({[{}()]<<>>}[(<><>)({}())])][([{<>()}{<>{}}]{(<>()){()[]
            [((<({(<<({[<(<>{})<[]<>>>[([])[(){}]]][{{()<>}(<><>)]<[{}{}][{}<>]>]}{<{(<><>)[{}[]]}(([])[[]{
            (([{<[<{<<{{({()[]}{<>{}})[[[]<>]<[][]>]}<{<()<>>[()()]}{<<>><()<>>}>}>>[<[[<[{}<>]{[]{}}><(<
            (<<<[<<<(([{{<()<>>[<>[]]}{<()[]>([][])}}])(([{{{}{}}[<>()]}({<>()}{[]{}})]<[<<>[]>]{({}<>){[]<>}
            {<<[<([[({({{<{}[]>([]{})}[<()<>>[(){}]]}{[(()<>)]{<()[]>({}[])}}){<([{}[]]({}<>))(({}{})(()<>))><([(){}]<{}
            {{[[<(<({<([[{[]{}}(()[])]<(<>[])>])({[[{}[]]({}<>)]})>[(<<[{}{}]<[]<>>>>{{{[]<>}[{}()]}({<><>}<<>()])}){([<
            {[[{{<(({(([[<()>{()<>}]([[]()]([][]))]<<({}[]){[][]}>([<><>]{()()})>)((<[<><>]{[]<>}>[[[][])[[]<>]
            [[<<{[[{([<{({[]()}({}())){{{}<>}<()()>}}([{{}()}<{}()>][{<>}[()<>]])><{{{{}[]}(<><>)}{[[]<>]<[]<>>}}>])}
            ({(({(<({[{((<{}><{}>){<{}()>[{}[]]})}]{[<({<><>}({}()))>{{({}<>)(()<>)}{<{}{}>{<>[]}}}]}}({[{([[]{}])[<()
            ({[{<<[<<[{{{{[][]}[()[]]}<<{}()>{{}()}>}{({<><>}([]))[<{}<>>(<>{})]}}{[([<>{}][()<>])([()[]])]}]<{[(<<><>
            {<{<(({[[{(({{()<>}[[]<>]}[[()<>][()()]])(<((){})[<><>]>{[[]<>][()<>}}))}<[<{{{}}[{}[]]}<(
            {{<[[(<[{<<((<{}<>>{(){}}))((<[]<>><{}{}>)({<>()}<()<>>))>>({[{(()[])<{}<>}}(<()()>)][<[<>{}]{<>
            <[<{((([<(<<((<>())<<>()>)>(<{<>()}{[]<>]>)>)[[{{{()[]}(()())}{({}())({}<>)}}](<[{()()}<()()>]((()){<>[]})><[
            (<([{[[[[[(<(<<>{}>)<{{}{}}>>(<<[]()><[]()>>(<(){}]{()()})))]]]({(<{(<()>([]{}))[{()<>}{[][]}]}([<
            (([({{{<(<{[<[<>[]](<>())>(<{}{}>)]<(([]<>>(()[]))(<()()>(<>{}))>}>({{[{[][]}][{[]<>}([]{})]}(<(()
            ({({<{[{{([(<(()())><{[]{}}(<>())>)(<[()()]{()()}>[[[]()](<><>)])]{<<[[]<>](<>())>(({}{})<<><>>)>
            ((([[{[<[{(<[{<><>}{{}<>})>{<[<>{}](<>)>[<[][]><[]{}>]}){[{{()[]}<<><>>}<(<><>)({}{})>]{<{(
            [[<({([{{[<{<(()())<[]<>>>{[[]{}]}}(<[{}<>]<<>()>><(()<>)[()()]>)>>{<{<[[]<>]{[]()}>{<[]<>>{[
            (({[[(([[<{([[{}<>]{()()}][[{}[]]{[]{}}]){[[()[]]<[][]>][<{}{}><{}{}>]}}[{[({}())[[]<>]]}{[({})<<>>]<{<>[]}""";
}
