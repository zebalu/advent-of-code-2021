package io.github.zebalu.aoc2021.main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.BaseStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Util {

    public static <T> Stream< T> drop(Stream <T> stream, int x)  {
        var it = stream.iterator();
        burn(it, x);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, stream.spliterator().characteristics()), stream.isParallel());
    }
    
    public static IntStream drop(IntStream stream, int x)  {
        var it = stream.iterator();
        burn(it, x);
        return IntStream.generate(()->it.nextInt());
    }
    
    public static LongStream drop(LongStream stream, int x)  {
        var it = stream.iterator();
        burn(it, x);
        return LongStream.generate(()->it.nextLong());
    }
    
    private static void burn(Iterator<?> it, int x) {
        for(int i = 0; i<x && it.hasNext(); ++i) {
            it.next();
        }
    }
    
    public static void main(String[] args) {
        IntStream is = IntStream.iterate(0, i->i+1);
        var dropped = drop(is, 3).limit(10).toArray();
        System.out.println(Arrays.toString(dropped));
        
        int[] c = {30};
        is = IntStream.iterate(0, i->i+1);
        dropped = is.dropWhile(i->c[0]-- > 0).limit(10).toArray();
        System.out.println(Arrays.toString(dropped));
    }
}
