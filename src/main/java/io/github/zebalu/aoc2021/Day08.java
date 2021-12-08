package io.github.zebalu.aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day08 {
    private static final String SEPARATOR = " \\| ";

    public static void main(String[] args) {
        firstPart();
        secondPart();
    }

    private static void firstPart() {
        System.out.println(INPUT.lines().map(s -> s.split(SEPARATOR)[1]).flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(Day08::isEasyNum).count());
    }

    private static void secondPart() {
        System.out.println(INPUT.lines().mapToInt(Day08::decodeLine).sum());
    }

    private static boolean isEasyNum(String s) {
        return switch (s.length()) {
        case 2, 3, 4, 7 -> true;
        default -> false;
        };
    }

    private static int decodeLine(String line) {
        var decodeMap = decodeMap(line);
        return Integer.parseInt(Arrays.stream(line.split(SEPARATOR)[1].split(" ")).map(Day08::arrange)
                .map(k -> decodeMap.get(k).toString()).collect(Collectors.joining()));
    }

    private static int byLength(String s) {
        return switch (s.length()) {
        case 2 -> 1;
        case 3 -> 7;
        case 4 -> 4;
        case 7 -> 8;
        default -> -1;
        };
    }

    private static String arrange(String s) {
        return s.chars().sorted().mapToObj(c -> Character.toString((char) c)).collect(Collectors.joining());
    }

    private static Map<String, Integer> decodeMap(String line) {
        var parts = line.split(SEPARATOR);
        var keys = Arrays.stream(parts[0].split(" ")).map(Day08::arrange).toList();
        Map<Integer, List<String>> lengthMap = new HashMap<>();
        Map<String, Integer> result = new HashMap<>();
        keys.forEach(key -> result.put(key, byLength(key)));
        keys.forEach(key -> lengthMap.computeIfAbsent(key.length(), k -> new ArrayList<>()).add(key));
        String nine = findNine(lengthMap.get(6), lengthMap.get(4).get(0) + lengthMap.get(2).get(0));// +upperLine+lowerLine);
        result.put(nine, 9);
        String six = findSix(lengthMap.get(6), nine, lengthMap.get(2).get(0));
        result.put(six, 6);
        String zero = findZero(lengthMap.get(6), six, nine);
        result.put(zero, 0);
        String three = findThree(lengthMap.get(5), lengthMap.get(2).get(0));
        result.put(three, 3);
        char upperRight = firstMissingChar(three, six);
        String two = findTwo(lengthMap.get(5), three, upperRight);
        result.put(two, 2);
        String five = findFive(lengthMap.get(5), two, three);
        result.put(five, 5);
        return result;
    }

    private static boolean containsChars(String string, String target) {
        for (char c : target.toCharArray()) {
            if (!string.contains("" + c)) {
                return false;
            }
        }
        return true;
    }

    private static char firstMissingChar(String string, String chars) {
        for (char c : string.toCharArray()) {
            if (!chars.contains("" + c)) {
                return c;
            }
        }
        throw new IllegalArgumentException("no missing char: " + string + " vs " + chars);
    }

    private static String findNine(List<String> culprits, String chars) {
        return culprits.stream().filter(s -> containsChars(s, chars)).findAny().orElseThrow();
    }

    private static String findSix(List<String> zeroSixNine, String nine, String one) {
        return zeroSixNine.stream().filter(s -> !s.equals(nine) && !containsChars(s, one)).findAny().orElseThrow();
    }

    private static String findZero(List<String> zeroSixNine, String six, String nine) {
        return zeroSixNine.stream().filter(s -> !s.equals(nine) && !s.equals(six)).findAny().orElseThrow();
    }

    private static String findTwo(List<String> twoThreeFive, String three, char upperRight) {
        return twoThreeFive.stream().filter(s -> s.contains("" + upperRight) && !s.equals(three)).findAny()
                .orElseThrow();
    }

    private static String findThree(List<String> twoThreeFive, String one) {
        return twoThreeFive.stream().filter(s -> containsChars(s, one)).findAny().orElseThrow();
    }

    private static String findFive(List<String> twoThreeFive, String two, String three) {
        return twoThreeFive.stream().filter(s -> !s.equals(two) && !s.equals(three)).findAny().orElseThrow();
    }

    private static final String INPUT = """
            fdgcea dgefa efabdg fdgcba dab baefc begd ebdfa db bafgdce | gfbcda cgfadb badcefg eacfgd
            eagfdb bafdec fdg edgca gf cdbafeg egfcdb bgaf aedfg defba | gfd edagcbf cdfebg gfedab
            faecdgb edgf decag cgf agdcbf aefcb gf dgebca fdaceg cefga | fcg fcbae fg aecgd
            abcgdfe gebfd eacbdf gc gbc cabdfg dcfab gdcfb gacd acfegb | fabdc cg bdfeg gbc
            bgcda cagdf geafdc fbcdge fedag fc egcabdf gfc gfedba acfe | cdegaf dbfgea adefbg afedcbg
            degfbc cdeg dcfbae debfc gbadecf begfca egb eg dbfge adgfb | egfdbc ecgfbd dbcagef cafbed
            gefacb cdg decgba badgfc edga eabcg dfacgeb dg efcbd dgbec | dg cgabe decfb dg
            bafdeg facdgbe dgba gcafbe bd bde gdbfe cegfd agefb ebcfda | gbafec bfgea fgecab cefdba
            bfcgea cadfb dfcg afbdcg cg ebgda bcgad acg edbcgaf ebfcda | acgfeb dgbca cegbfa cadbf
            bfeadcg gdcea bcagfe becdaf fcgbe bcd bd cbfdeg fdgb dbegc | dcb acged edbcfa dbgecaf
            bedafgc bagfce cadgf dbfcag dbca fbdga fgaced agb ab ebdfg | agb ba gdbaf fcbgea
            abcgf agdec agbcfd cfd df aefcbg decfba gafcd fbgd dbafegc | fcbag cegad gdace cdf
            cgb ecafdb bfdac cg cgfbd cbfega edbfg fbeagdc adcg dgcfba | gbfed fecagb fdbcga afcbd
            agcb cfaebdg bc bfedcg dcb degcab beadc dgeca ebfad fdgcea | acbg abcg gcba bc
            cgbf dafgce fadgbe bdecfg gdfbe cedab cg gce adfgcbe egdbc | cg cg ecfadg ecgdabf
            cd fecagd fbgca cdebfg cgfdb gdebf cbed gdcbfea gdefba dfc | cfd edcb bfdage edcb
            agbedc dcagbf af bcdaf bdagc gfbcae fgda bfa cbedf gcedabf | af gabecd dgfcaeb afb
            dgebfa adg gbed debfa dg adfgecb fcgea acdbef cafbgd gfade | adgef gd cgfbdae dgfacb
            fbc bcfgd fgdeba bdegacf bagfcd cbag afdgb bcdeaf fcgde cb | agfcdbe fcb cbf bc
            cgabd bgafc fcaedb bda bgecfad bfceag bd dagec fdbg dafgbc | cdgbefa bfacge ebcfga gfcab
            fba dabcge beacfg bdfc bagfd dcagb fgaed bf fbdagc dbcefag | gacbd dgeaf cfdb fab
            cebgd ade cbda cgfea dfabeg eadcg agdbec bgdecf ad deacbgf | ecadgfb dabgef beagdf ecgbd
            aedgc deafbc fgaedcb cdgfbe acfg edgfc ca cda dcgefa adbeg | acgf edcga cabgdfe gacf
            bcagde eg bfcea abgcd gdfcbe bge dabgcf bfegcad gceba aedg | acdgfb fbdgec agbec dbcefg
            ebacd decgb ebfac cbgdea adgb efabdcg bfdgec eda cfdaeg da | egdcb bgcdef da da
            cfgebd afebd cb cebda dgcae afgdcbe dgafeb bfca ceb bdcefa | bec gaced afedb eadgc
            bdcfea ge acfbg efagb bdaef badgefc gadbef eag gfde bdcgae | bcdaef bcefad efcadb gae
            befagd fd cbefa dcbega egacfbd efabd edcafg dgbf gedba fed | fde abedf def aedfb
            bcd abgcfde dbaf abecdg db dcbef abegcf efacb fabedc dgecf | ebcfd bfda dcb egcafbd
            fdbea caefbd gcdafe ecafd efb cedb be geabdcf gfbad fcaegb | baefgc defcab eb cadef
            dagec fadge dc cdg egacb adecfg bfgdeca fadegb dcabgf cdfe | fcde agcde efcd cgd
            dceabg fbaedc aegfdcb fc cfa cefgad bfadg bdcfa cbef eadbc | fc dcabf cebf afdbc
            defabcg febdg bdfega adfg fabcge edfba abedfc bdcge fg gfb | fgb fg fbdaec daefb
            fd gfbaec cdf fgced eacfg gfdebca fgda bdfeac cegdb cfadge | dfc gcfedba dfecg agfd
            ec bfeacg bdfca acdfe fagdbc eca febacdg ebcd eadgf cabedf | dfcae fecabgd eac aedfgcb
            edg dfceb egdcafb gcef dbcga cdafeb cgebd gdfbec eg gfdabe | fcdbeg ge bfaecd ebfcd
            ecg bcadfg egfac fgdca fgeab cfdage bceadg fced cgdebfa ce | egfba cfgae dcfe ec
            cdeg ec eac efdac dgcaf bagfec dbfeacg aegdfc bfdgca dfabe | efbad egdc afcbdg ce
            gbecda cbe egfcba abcdg agfcebd ecda bfedg adgcbf ce bdceg | ceagbd gdabc aced adec
            ecbagfd egfbd gafe gcadb bdgae gfbdea gefcbd bea ae cfabde | gdacfeb abedg fbgdec cbedagf
            bagfe fdeagc agfebd fbcea dfbca gcfbae cae bgce ec fgacbde | acdfeg ebagf ce efcgba
            bf dagcebf fdegc ebcfda geadcb bfc acbfgd acbdg fbga fdcgb | acfbde dbacge cadgb gfab
            cagbf bcde bdefag gcfde cdbefg deafgc bfd bdgfc bd dagbcef | fagdec becd dfb fgbca
            dbc db dgacbe bgced abde cdafebg dgfeac ebcfg daceg dcfgab | dgcfae dcb daeb bd
            bgdfca bag bacefd cbged acbgd faecbg fagd ga cgbafde fabdc | bfdagc acdgb bacdg dfga
            eafbcdg ebcafg bdge dga eafdg eafdc efgab dg agdfbc fdaebg | bcedgfa fgbcda bcgadf edgfab
            fc gbced efcgb fbage dbefac cgdaebf fcb fdebcg gacedb dcfg | gfcbade dafegbc adfgbce fbegc
            ebc cgbfe be bfcdga acfbegd abge abcfg dgfec cfdeba acbfge | dcegf eb badgcfe gabe
            decfbg cdbeag decgb fgebc cbf bf agefc fbdgcea becafd gfdb | dceagb dgbf bf fcb
            gcbfad aedcg egadb ecgdaf dcfgbae dc cefga fcde adc fabcge | acd bacegf gcefda gdeca
            cad fdcbega gedbc dbgfac efdgcb dbeagc eadfg ca daecg cbea | ca abce gfdea gbcfed
            afdegbc efgbad fcdge cafg fg efadc geadfc gfd bcdeg cbedfa | gf fgbaed fgabcde cdfge
            cefga fda gafced facgd degf fd efdgacb cebfag gbcad deabcf | gbecadf gfabec afd afgcde
            bedcf adc cdegbf aecb gcbeadf ebacdf ac eagfd cdfea cgdbaf | adc cbfgdea dca abcgfd
            dgab cfbdea gcadbef afdeg ebdfa fgdecb deafbg edg gd cgfae | ebdaf abdg gde cgeaf
            dfbae cgadf abfdgce ge ged aegfd debcfg eagdcf geac dfbcga | deg aebdf gcafd egac
            eagdb ecd afdc cd cfadgbe gfcdbe bfcgea efcdag adgce gceaf | fedcgb ecdga facd cd
            adbgcf fabced gfc afeg dfaec gecdf fg gfcadbe bdecg acgfde | cgf gfc decagf cdfae
            fgcda bfgcda bdecaf adc bcfag bdag beagcf ad fgcadeb dcfeg | dgfcba cad fcagd baecfd
            dcfe cbagd gdfabe ed abdfec ceabd bde becgaf eadgcfb fabec | dfce eagdbf faebc gacefb
            acefbgd abgfe cdgfbe abd fabdce dbfec da cgfdba edac dabfe | fbaeg eabcgdf eagfb afdbe
            ecgdfa bdfge bafdg fadcbe bfa bcag ba cgdaf geafdcb agfcdb | fadcg fagecbd baf cfeagd
            bcaegf dcfag def bedg gbaefd faged dfceab ed abgef fecbdga | ed dcagefb dgeabfc bdfeag
            ecfgadb fcb fgeac fdeba cfbead cefab cdba ecfbgd bc bdgfae | bfc gaebdf cfb afedcb
            fecbd efcgabd cfb cf dgbef abdce cbfgae faegbd fbcedg gfcd | febdg bdfegc gfcd defgb
            aecdgfb gedfbc agbf cefadb acf adfcbg fa dgfbc egcda dafcg | agfb gaebcdf bgedcaf caf
            gc dcbg eagcdb gce cfbgea faedg dceba abdcef acebdgf edagc | bdgfcea dbgc cbgfea bcaefd
            gcadb fegadbc afb ecgbfa cdbf bcfagd efagd bfagd abgedc fb | bfcage dbfga gadbf bagdf
            gadbefc abgdef gbcfde cef gbfae cdbaf ebfgca abcef ec ecag | fdbca acdbfeg ec ce
            adbec cgabfd afcbge dfbgae efcbagd bgeca gc agc bagef gefc | gafcbde gca cga dgcbfa
            fdcgba cedab debga cebg adbgef edabgfc bgcade cfade bc acb | gedcba cgbe cdgeab bc
            cedagf gcebda gacf afedc aefgd ca cea gdafbe bdgfcea ebfcd | ac cagf fbaedg afedc
            fa ecgbfa egabd gedafb dcabfeg gfa dgefa bdaf dcfeg agbedc | cgfbaed dbacgef aebgcf gfa
            dgebaf cfbgd dfaecb ef gacefbd bdfce faec ceadb feb cedabg | bdefcag bgfcd ef cdebf
            cgad afcbed fdgaceb bfgead egbadc cbedg gd bcade fcegb dgb | feadgbc gbedc bgeadc abdgef
            dbefga cbfad ebdcf gdabefc bfe gcfde cgfeda be cgdefb bcge | cgbe eb cebg geabfd
            fecda begacf bagfedc ga cag gfcdbe cebfg gabe fgbdca cgefa | efcbg gca eacfd dgcebf
            faegcb cfdbe gdfca afecgd cgbdaf adbg gdbfc bfg gb gcdebfa | fbdgac cfdga cadegfb cgfbd
            gdefac abecg dgebfca bf defb bgdeaf fgdea abf afegb cbgadf | fdgeab efbd fb abgce
            gcaed bdfgeca eafcgd dfegcb fdgce cae befacd ea dagbc feag | ecbdfag gbdac gcbedf cgfebd
            cge ce aedcg fagdbc dbgcef cbdaeg abec dfega egdbafc agdcb | dagfe ec adfeg ce
            dbfeg ebgdca dgfcab fcae bdfeagc abf gbeca bacfeg af gafbe | afec bfa fgbed fab
            gfc dcgae fdgeab abdfg bafc cegfbd cagfd fc fdgcab gbacedf | faedbg gfdba gfadb ebagdfc
            dbcega cedba cdg bgfca gd bagcd dfgceb becfda adge cdbgfea | gcd abfcg ebdcfga dbgca
            dbf gdafec aegbcdf db fbcdg afgdeb bfgce cdba abfdcg acdfg | egcabfd egcfb cdgfba fbd
            gdbca acbfg efcgab eagdfbc edgabf dga eabcd gd bcagdf fgdc | gad dagcb dcfageb cgafb
            cebfdg ecbg gdefb dgc cg gecdf egafdb fdcbeag abgcfd eadcf | dabgfec fbgacd badfgc abcdegf
            gfb gfcbe egadfb bgcde bdfc cefdbg cfage bfgceda bacdeg bf | fb fb fbg gcebd
            edcgfa abecgf dfagbc aef ea acbe fbgcead fdebg fbaeg gabfc | fae ceab fcageb ebfag
            bedfcg fgcade caf aefbg dgac ac efdgc fecga fgbecda bfedac | gfbea acf egbfdc agcd
            ecgda gab dfab bcgeaf fadegb ab gbfed dbgae dcebfag dcefgb | ebgfd dgbae gbfde gab
            aebcdg cgfeabd degba fb dafegb fdcga fdgab gbf abef ebcfdg | eafb eadgbc fgb dgfac
            gc cafbeg cdge abdfc fedbg cbg dbgefc edbfag gbcfd gefbdac | fceabg dcge cg gc
            eag cafde debg fbedgca bcgadf egafcb gdaec gecbda cdagb ge | gcabed gbcda gedb bdcga
            ea ceafgd bdae dcbage gae fgecb agecb abcdfg cgdba eabgcfd | cefgb dcbfag acgdef adgcfe
            fbgacde dbfeag bdgcf gaf ga gdacef afcdg afecd aefdbc geac | afg fga gacfd fgaced
            dfga acd dgfec acbged acfbe afdce bcdfeg da fagecbd decfga | cda aebgdc fagd caebgd
            ebgf edacfg acfedgb aefcb fcg gf ebcafd abcdg cfabg fgecba | cdfgae dbgca deacfb gf
            gacbfe abgef cbagf gecfdb fdbea agcdbf eg cgefdba egb agce | efdba eafgb dabfgec cgfadb
            afbegd acdgb aeg cegdfa aebdf dbafec gbdafce eg bfge bgeda | afbced bdfagce dacgb bfeg
            cag gc agcfde agecbf bceg acebfd agfbd efbac edgfbac gafcb | bfcag cgeb agc cgfabe
            ecadgb abe fgeb gbaedf adbfe fdage dgcafe bfdca cfgbade be | ebfcgad aedfg cafdeg dcefga
            eagfdbc fbcgea bgf afdbc bdgec faebcd fgcbd fg bgdcaf gdfa | dfbcae fdgcb efacbgd bdacegf
            dfag cdfegb fa edfbac cfa egcba fdgcb bfgca bgcdafe dgcfab | fgbca cbfagd acgbfde fca
            fb bdfg gfebc edbcg beadgc adfceb gcfea efb febcdg fdbecga | agcfe bf gbcef gdbec
            ecfgabd ebfcg decbaf cgfab ecfbdg be bce gedb dcgef fdcgea | begfc acbgf gfcba eb
            cg ebadgc dceba fegcba bcdfea acgdbfe cegd bcg abdgf dbagc | abcde cg gc gbc
            bfc acegbf febacd ecdba cf dcfa dbcef dfgeb bgdcae bcfdega | fbcgea fbc fbecda fdca
            agbfd gfeabc ad dfa adgc bagfc gfbacd gfdeb fegbadc fcdbae | fad dgca gdefb afbcg
            bcgead ecagb fcagb gcbedaf dage ea bea afedbc cdgfeb cebdg | gbdec eba edfbca agfdcbe
            abcg age eadcbf ga gdeafb ecfgd becda cegad efbcdga adcgbe | eabfdg fdceg cedga cgdae
            fbd cbgd dgcfa cbdaf fbdage db gecafd bacef cdbagf fcdbgea | facbd bgdc db cbaef
            fbdca fdcgeab gfbc gcbad gdc abged bafdgc gc gcdfea cdbeaf | adgcb fegacdb cafdeb geabd
            bdafc cegbdf gd gbafec fgecb fdg aebfgdc dbgfc abfdge edcg | cdfbg dg edcg afdbc
            gbaefd caefg dbgae fb ebafg cagdbe bcadfe fgdb ebf cedfgab | gedcba bf fgbd bdfeagc
            da abcfdg gaecf bdaf bcdgea gecafdb cad cgbefd fgcdb cdgaf | dbcaeg bdaegfc dbfcage bafd
            gecba aegcf aefcbd gecdbf af gdfa cfa fgaecd cbdfega dcgef | fa eadfbc cbega cfage
            be agfedc fdega egcfadb fbeg eba cfdeab afegdb dbega dbgca | adgef gebf gdfbace gfeb
            fg gfe bgfae gdfa dabcfe ebafdg fdcebg abdef gbeac bedfacg | gef adfg fg fge
            ad fdgceb becdag gecfd afdge daecgf fbegdca afcd dae geabf | cadf dbagce ad dfac
            fgaedbc dbfae ceda egbcf bacfdg efbdca fdeabg ca cfa cfabe | gdefbac abfed ac dfcaeb
            fgbdca cdaefg bafge ac gca cgebfd bafdecg cadb gdbcf bfacg | egdcaf cgebdf gca agc
            cg cdabfe bfaec bagfce gfdabc fcg efagc ebfgcda edfag cgbe | bgce cgf gfc gcadbf
            caeg gaf gfcade cgfad bedgcfa fdcea ga bdgfc aefbdc gdefba | cfaed cefad fadgeb dgfca
            fbgad gbf fcdabge cegfab gadbc gfead fb aedbfg befd adgcef | gabcd bf acgdb bf
            gebaf bgeadf gcabedf edfca feadg dg gfdbca eagbcf gdbe gad | egdb ebfag dga ebfacg
            dcgabe gcb adcfbg agfbe bc afgcd bfcga adfecg bcdf bfacged | dcgfa cb dgabfc cgb
            ebafc debc efgdab fcb aefbd gaefcdb bc dcgfab abefcd afecg | agefc baecf ecbd bcf
            gfbdc de badfcg dge befga gfebcd cdef egfdbca ebdgf bgedca | ecfd dfgcb edfcabg dcabgf
            gaceb bad dfgabe dbfecg fdebg cebagdf gdbae adfg dbfeac da | eabdg dgfa afegdb agebfcd
            dfabg gdfeb da gdcabf abgfc bcgafe fadc adbgec gda gcebdfa | gda ad afcgb efgbd
            fdcaebg dbagfc edagb ca gacbd gcfa acedbf gefcbd cab cdbfg | cba geabd cbgda bac
            dfegc agc ca bacfgde adfgce adceg fcad efbgdc geafbc daebg | gdaeb gbade gca gdace
            bfedgac gedb abefg dacef gedbaf gadfbc db dab cgbefa afbde | edabf gfcbad dafcgeb bad
            fcbgea gba decgba gfdace gdeac bgdac afcgedb bdge dafcb bg | begd gcbfdea bg daegc
            egfab afc cdgba gfcbad cbgeda bgafc dfabec gfdc bacefgd fc | fc bceafd gabcd dfabcge
            cf cdabge cegda fgec cbfade acf eagfcbd acgfd bfgad gcfaed | cefgdab cdaebg gedfcab facegd
            ca ebdcf cda dfeac fdgeba agfebdc dbcagf dfage afecdg gaec | eacg adc efdgab dac
            dc gfcdeab cgfaed facge gdeba dfcg dca bfaced gdaec aegcfb | fdcg fbecag egdafc efagbc
            dbcfa fdaecb afg cafgbed cadfbg ga cadgfe cbga fbedg dbfag | bdegf gcaefd dagbf fdeacb
            defbgc cfgeabd cefgd gdc eabfgc afedg afdgcb dc bcefg cdbe | gadbcfe ebfgc decfbg gfecdb
            egad fedac dgfeca cdg efacdb dcbafg fgcbe gd dcfeg cebfdga | cgd dg gdc gadbcf
            dfe gcde dgfbe fbecad gdbaf faebgc ed gfebcad ebfgdc bgfec | cdeg gdec ebfdgc ed
            dgabefc cdbefa aedfb abdc becfd fagedc dcf cd bcgfe fbgead | fdc dfc bcad dc
            acgbf ebgfad bgcad cdbge dfcebg dcaebg ecda da fbcgdea abd | facgb gacdb egcabfd dcbgef
            acbe afgbcd adc bedcag ebgcd gdeac eacgbfd fedga ac gfbcde | dcbeg aceb gbdce fedga
            gfabd gcedab eb ecgdbaf ecdb egdacf egb degca bdega bgfcae | be gfacedb cgdaefb ecbagd
            fadgc dgfe eafdgc agfedbc gbdeca gcafe gd cbgeaf bdafc dgc | gecdabf dbecga efadcg dafecg
            bgc aecfg fadceg bcgeaf fdagb afgcdbe febc bc fagcb egcabd | cfbe cbfag bdgfa cgeafb
            fadecg cea dagcf cfed ce bdgae geacd eabdgfc afbcge dfcagb | adgce dacfg gcaefb ce
            cb dcgb fbc abgfcd ebdgfa acdfbe efgca afdcbeg fbagd cfbga | cb bdfcea fcdagb dgcb
            gadebc ag cabgd acbed dgbfc eafbcd bfadecg abcegf bga aegd | aegd bedacf gade egad
            fdagc acegd aecdfb adfbeg abdce becdga egd agbdecf ge gceb | acdeg fdbaec ged gebc
            ade afbceg aefcg gdac da dgfae gbdef acdgef bdefgca adbfec | bgcfae ad bdgefca dgfae
            fdcage eafcg df fgecd bcafge cdf cgdafeb gbdce dbagfc fead | cegfa cgedb fd gaefc
            ebdac cbaefd dbaeg gbafec cfed ecfab bfdegca dcb bfacdg dc | dcagfb caebd fcbade bdc
            gdae eac ecdfgb afceg efdgc acefdg fbcag ae cfabedg abdefc | afdbcge eacfdbg cae dega
            facgb eacbdg efdcab cg acefbgd bafcd efagb cfbdga gcdf bcg | dbafc eafbg gdbace bgc
            cdb bd ecdfgb ecdbg dgcfe becga cbgadf fdgcea dbecgaf defb | bd bcgafd cbgafd gbdcfe
            dgfebac dcb dagec cgbe afdbe dceba dgbaec dcgfba fedgac bc | gecb bc gbce bc
            ecadbg eabgfd gfbc abgdcef dbcge bf dcebgf cdbfe fbd fdaec | cegfdab gdfeab fbd gfebdca
            gcdefa edab ed ecd cdbfea dfecbga cebfa dcbef abfgce gcbdf | bfcdg agdfecb gcabfde acgefd
            begacf bafce cfa fc beafd agbce cbgf gcdeba cfdaeg fcebagd | afecbg gabcfe fc cf
            dbacfg ab adbf caegfb abc gcdeafb dfgeca dacgb gcafd cebdg | ab bacdg ab gdbcfea
            dfga afcbd fbcag ceagdb fagcbd dac da gcaefbd fbdec ebfgac | dfecb bfcga da dcfeb
            bdeca bcfdg feabgd bgfecd eacfdbg cdfbag fgce debgc ebg eg | ge adfgcb gdaecfb eabdgf
            dfe aefcbgd dcgbe df dbfc gcdabe cedfbg gfbed abgfe efgadc | bfgced ecgbd egcdfa fd
            adebgf dbcae af dbgeac gedbcfa cdgef efa cbaf dbcfea efcda | acfb ecgabfd fa fea
            gd efgab egdf daegb bfgaedc edacb abcegf fcdbag gda agdebf | dag gbadfce gd bgdeafc
            dgcfb bcgafde fdc cfbeg gdefac fd gfbaec dfebcg befd bgadc | fd ebfd fd defb
            eafdg cdfgab bdac gfbda fdbgc abgfdec afb cbdgfe ab gefcba | efabcg abdc gcfdb afdge
            feadcb gdcbfa dbafc gfbdcae fedga aceb cgbefd faced ec cde | efdca cdaef ecbgfad fdcbag
            gade feagb dgefb fga bdfecg dgbacf ebfgda ga cbeaf bfeadgc | ebgfd cagdfb gfbdeac ag
            ebgdacf acdgb dfg dgacf cgefbd acgfed bgcafe fd cfega feda | cgeaf fcdega cadfeg dfgabce
            dcea egcfba de gecabdf degbfa fcbgd egd begca degabc bdgce | bgdeaf deg aedc edca
            fdagec dbfa acf af afbecdg edbgcf fgbcd bafgc bdagcf baceg | af acf bdfceg edbcfg
            gfeac bgfaec gdaebfc cb aefgdc beadf ceb acdbeg bcefa gfcb | cfebgad egcdba ebcfa bdgeac
            cfdbg dfbega agfbdc cdfa dagcbe dc dbfag dcaefbg begfc cdg | cd aegcbfd fbadg fdacebg
            fbagdec gbadfc egb ecgfba egfab gebdac fgec fbdae cgabf ge | abedf caegfbd afgcb beafcdg
            bgeadcf acdgb gfadbe fgcab efbcda dg bgdaec ebacd dgb cged | dg dbg gd gd
            dcbeg gab bafegcd cgdba acgefb bgafcd acfgd dfba ba eafgcd | dgcefa abg adfb gba
            fb acfgd fabedc acfbd debca gdcbae efbdgca afeb gdfebc bcf | dgafc agfcd fb fb
            cedgfb egdca gcabde ac dbecg cfbead bcedgaf gfdea cda agbc | ac gceda gbca agbc
            ceda cebagdf ec fagde cbfged fcedga gaecf ecg dbgefa cabgf | dace dacgef gec gfcedba
            ge egab febad fgaed fdgaeb afgdc fcgedb ged gadefbc bfedac | ge acegbfd egab ageb
            dgebfca fcd gbcfa cfadb cdfbeg fegcab adfbe bcfdga dc agdc | fcd ebfad gfcbda fcd
            cbgafd fgea bfedga dfa fgedbc bfdae efbdacg af febgd bdcea | dafbe dgbcfe efga af
            edcfba agfbd agcdfbe abg ga ecabfg aedbf aegd gdbcf fdaebg | faedb bag edfab fcdgb
            bed fecadg dabcge cadeg cgbd afcbe gedabf bd cabed faedbgc | deb fcdgeba gbeacd cedagf
            aefdcbg gedbfc fdg cgfb dfaeb fcagde dgcaeb gcedb efbgd fg | gdf bgdec bcegd gbedf
            beacdg gadcf bdcgefa bgdcfe fb eacbg beaf febacg bafcg fcb | dcaegfb cbf egdbca dgcfa
            dgcaf bdgaef egfbdca aef edcabf ef agdbe cagebd feagd efbg | badecg egfdba dbegafc gfbe
            ecfab fdgae cbdefa dafce acbegfd acd cbed cefgba cd acdfbg | ebcd fceadb cbeagf bdacef
            cdage aecfgd afecbdg dcafg adgcbe eagdfb fd gfcab edcf fdg | cgedab gdf fgd gdf
            gabfc bca cbefgd cgea abdecf ca cbfgead caefbg gfceb abfgd | begcdaf ca ceag fgceb
            cdfbgea gcad dfcbg cgbfea agf bcadgf fdabg aefdb cedgfb ga | gbdcf cdag bcdfg fdbegc
            fc gbefdc abfecg egcab aefc acgfb bcf fbeadgc adbegc agfbd | cfgba adfbgce cf cbagf
            beadcg cgbf dfegb bg adbfecg gecfdb fegad bfced eabfdc egb | gdfae cgbdef gadef gfcb
            gbeadfc dcebg gc dgac adegcb egbfd gec afbdec decab afbgec | egabcdf agcedb cfegab egbdc
            adgebc gea afcdg fgbade dbaec bcdfae cdfgaeb gcaed ge ecbg | ega dagebf gdfbae gbdeac""";

}
