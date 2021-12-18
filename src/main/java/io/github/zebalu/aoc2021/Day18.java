package io.github.zebalu.aoc2021;

import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Optional;

public class Day18 {
    public static void main(String[] args) {
        var nums = INPUT.lines().map(Day18::read).toList();
        firstPart(nums);
        secondPart(nums);
    }

    private static void firstPart(List<SnailNum> nums) {
        System.out.println(nums.stream().reduce((l, r) -> add(l, r)).orElseThrow().magnitude());
    }

    private static void secondPart(List<SnailNum> nums) {
        System.out.println(nums.stream().mapToLong(l -> maxMagnitude(nums, l)).max().orElseThrow());
    }

    private static long maxMagnitude(List<SnailNum> nums, SnailNum left) {
        return nums.stream().filter(n -> n != left).mapToLong(r -> add(left, r).magnitude()).max().orElseThrow();
    }

    private static SnailNum add(SnailNum left, SnailNum right) {
        var added = new PairNum(left.clone(), right.clone(), null);
        while (added.reduce()) {
        }
        return added;
    }

    private static SnailNum read(String line) {
        return read(new StringCharacterIterator(line));
    }

    private static SnailNum read(StringCharacterIterator siterator) {
        if ('[' == siterator.current()) {
            siterator.next();
            SnailNum left = read(siterator);
            SnailNum right = read(siterator);
            siterator.next();
            return new PairNum(left, right, null);
        }
        if (',' == siterator.current()) {
            siterator.next();
            return read(siterator);
        } else {
            StringBuilder sb = new StringBuilder();
            while (siterator.current() != ']' && siterator.current() != ','
                    && siterator.getIndex() != siterator.getEndIndex()) {
                sb.append(siterator.current());
                siterator.next();
            }
            return new SimpleNum(Long.parseLong(sb.toString()), null);
        }
    }

    private static abstract sealed class SnailNum implements Cloneable permits PairNum,SimpleNum {
        protected SnailNum parent;

        abstract long magnitude();

        int depth() {
            return parent == null ? 1 : parent.depth() + 1;
        }

        abstract boolean reduce();

        abstract void add(long value, boolean left);

        abstract boolean shouldReduce();

        abstract boolean canExplode();

        abstract Optional<PairNum> getExploding();

        @Override
        protected SnailNum clone() {
            try {
                SnailNum clone = (SnailNum) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static final class PairNum extends SnailNum {
        SnailNum left;
        SnailNum right;

        PairNum(SnailNum left, SnailNum right, SnailNum parent) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            if (left.parent == null) {
                left.parent = this;
            }
            if (right.parent == null) {
                right.parent = this;
            }
        }

        @Override
        boolean reduce() {
            var exploding = getExploding();
            if(exploding.isPresent()) {
                exploding.get().explode();
                return true;
            }
            if (!left.reduce()) {
                return right.reduce();
            }
            return true;
        }

        @Override
        long magnitude() {
            return 3 * left.magnitude() + 2 * right.magnitude();
        }

        private void explode() {
            if (depth() > 4) {
                addTo(((SimpleNum) left).value, true);
                addTo(((SimpleNum) right).value, false);
                unlinkMe((PairNum) parent);
            }
        }

        private void addTo(long value, boolean left) {
            findFirstNotMe(left).ifPresent(found -> found.add(value, !left));
        }

        private void unlinkMe(PairNum currParent) {
            if (currParent.left == this) {
                currParent.left = new SimpleNum(0, currParent);
            } else {
                currParent.right = new SimpleNum(0, currParent);
            }
        }

        Optional<SnailNum> findFirstNotMe(boolean left) {
            var currParent = (PairNum) parent;
            var curr = this;
            while (currParent != null) {
                if (left && currParent.right == curr) {
                    return Optional.of(currParent.left);
                } else if (!left && currParent.left == curr) {
                    return Optional.of(currParent.right);
                }
                curr = currParent;
                currParent = (PairNum) currParent.parent;
            }
            return Optional.empty();
        }

        @Override
        void add(long value, boolean left) {
            if (left) {
                this.left.add(value, left);
            } else {
                right.add(value, left);
            }
        }

        @Override
        boolean shouldReduce() {
            return depth() > 4;
        }

        @Override
        boolean canExplode() {
            return depth() > 4 && left instanceof SimpleNum && right instanceof SimpleNum;
        }

        @Override
        Optional<PairNum> getExploding() {
            return left.getExploding().or(()-> canExplode() ? Optional.of(this) : right.getExploding());
        }

        @Override
        public String toString() {
            return (depth() > 4 ? "![" : "[") + left + "," + right + "]";
        }

        @Override
        protected PairNum clone() {
            PairNum clone = (PairNum) super.clone();
            clone.left = clone.left.clone();
            clone.right = clone.right.clone();
            clone.left.parent = clone;
            clone.right.parent = clone;
            return clone;
        }

    }

    private static final class SimpleNum extends SnailNum {
        long value;

        SimpleNum(long value, SnailNum parent) {
            this.parent = parent;
            this.value = value;
        }

        @Override
        long magnitude() {
            return value;
        }

        @Override
        boolean reduce() {
            if (shouldReduce()) {
                long left = value / 2;
                long right = value % 2 == 1 ? (value + 1) / 2 : value / 2;
                var replacement = new PairNum(new SimpleNum(left, null), new SimpleNum(right, null), parent);
                PairNum p = (PairNum) parent;
                if (p.left == this) {
                    p.left = replacement;
                } else {
                    p.right = replacement;
                }
                return true;
            }
            return false;
        }

        @Override
        void add(long value, boolean left) {
            this.value += value;
        }

        @Override
        boolean shouldReduce() {
            return value >= 10;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }

        @Override
        boolean canExplode() {
            return false;
        }

        @Override
        Optional<PairNum> getExploding() {
            return Optional.empty();
        }

        @Override
        protected SimpleNum clone() {
            return (SimpleNum) super.clone();
        }
    }

    private static final String INPUT = """
            [6,[[6,3],[[1,4],[8,4]]]]
            [5,[[[0,8],[1,0]],8]]
            [[[6,[7,7]],[2,[6,4]]],[2,6]]
            [[[[7,4],[2,7]],[4,[1,5]]],[[[0,5],5],[[2,1],[8,2]]]]
            [[[[5,9],[7,2]],[0,[9,9]]],[[[5,3],[7,9]],[3,[9,1]]]]
            [5,[[3,0],[[8,2],5]]]
            [[2,[[0,8],7]],[4,[7,7]]]
            [[[[3,4],6],[5,[4,2]]],[[9,[9,5]],2]]
            [[7,[0,5]],[[1,3],[7,[4,0]]]]
            [[[6,2],3],[[[2,0],9],[5,[7,2]]]]
            [[4,[8,6]],8]
            [[[9,8],[[7,3],[4,6]]],[5,3]]
            [[[0,[8,4]],8],[[5,[4,7]],[5,9]]]
            [[[[0,8],[3,7]],[5,1]],[[5,6],2]]
            [[[[5,8],0],[[3,0],3]],[[6,5],[[8,0],[3,9]]]]
            [[[8,[5,6]],[6,4]],[8,0]]
            [7,[[5,[3,8]],3]]
            [[[4,[2,0]],2],[[[8,1],[5,8]],5]]
            [9,[[[6,6],[8,1]],[[7,9],9]]]
            [[[6,0],[[7,2],9]],[[[7,3],[1,1]],0]]
            [[[[7,8],[0,3]],[5,9]],[[2,[4,3]],7]]
            [[[3,1],[3,[6,3]]],[[6,[8,9]],7]]
            [[[[1,1],[0,5]],[8,1]],[0,[8,[1,4]]]]
            [[[6,[8,6]],[7,8]],[[7,3],[3,[5,8]]]]
            [[[2,5],[[6,8],[4,5]]],[[2,[8,2]],[2,[3,2]]]]
            [[[[9,2],0],5],0]
            [[7,[[3,7],[0,9]]],[6,[1,[6,9]]]]
            [[[[4,5],5],5],4]
            [[[6,[6,9]],[8,3]],9]
            [[[[2,7],[8,6]],0],[2,[4,9]]]
            [[[4,[9,8]],[7,6]],[7,[[2,7],[2,7]]]]
            [[[0,8],[4,[5,9]]],[[4,[1,0]],[6,8]]]
            [[[2,4],9],[[[7,9],5],[0,5]]]
            [[[3,[8,6]],6],[[8,[6,7]],[[6,1],[2,1]]]]
            [[[0,[0,5]],[[0,5],4]],9]
            [[[[0,0],7],8],[[8,[4,6]],9]]
            [[[1,[1,1]],[3,[2,5]]],[6,6]]
            [[[[3,7],[6,1]],[5,4]],[[0,[2,6]],[0,1]]]
            [[1,1],[3,4]]
            [9,[[4,[7,8]],[3,4]]]
            [[[[5,3],[5,9]],9],[[[2,4],[2,7]],[[6,3],[1,8]]]]
            [[[2,[2,2]],[[8,7],9]],[[[4,6],[5,3]],[[2,6],9]]]
            [[[3,8],[[5,7],7]],[[[0,9],3],1]]
            [[[6,[1,9]],[2,1]],[[[7,0],[2,1]],8]]
            [[[8,[9,9]],1],[[4,1],[[2,8],1]]]
            [[[2,[3,7]],[[2,4],[3,5]]],[[3,[1,9]],[[1,3],[1,7]]]]
            [[[[4,3],8],3],[[6,[1,7]],[[4,2],9]]]
            [[[[1,9],1],[[0,7],[9,4]]],[[[7,2],[0,1]],8]]
            [9,5]
            [[[[6,4],4],[[3,4],0]],[[9,[7,6]],[[3,4],[7,1]]]]
            [[0,2],[[[4,9],[3,4]],[2,[3,9]]]]
            [[[[8,9],9],[[6,4],[2,9]]],[[4,5],[[1,8],2]]]
            [[[6,[9,5]],[4,[1,0]]],[[[4,1],[3,5]],[3,3]]]
            [[[7,1],[[5,4],8]],[[0,[9,4]],7]]
            [[[4,[0,3]],[[0,2],8]],[[0,[9,6]],[[6,3],[3,2]]]]
            [[[[5,5],8],[[4,5],3]],[3,[[0,2],0]]]
            [[[[9,5],[1,0]],[[9,1],[0,9]]],[[1,[9,1]],[1,3]]]
            [[9,[[5,7],8]],[[9,[9,3]],[3,[0,1]]]]
            [[[5,6],[9,8]],[2,9]]
            [[[9,[3,8]],[9,0]],[[8,[6,2]],1]]
            [3,[4,[1,[0,4]]]]
            [[9,[[8,5],[8,0]]],[[1,6],[8,4]]]
            [[7,[[6,8],5]],[[9,[1,3]],[[6,5],[0,8]]]]
            [[[6,0],[9,[3,5]]],[8,6]]
            [[[1,[2,3]],[[5,2],4]],[1,[[7,3],2]]]
            [[[2,[1,1]],3],[[8,[5,5]],[[7,5],[8,9]]]]
            [[[4,0],[8,6]],[[[7,1],7],0]]
            [2,6]
            [[[[5,4],[9,7]],4],[0,6]]
            [[4,[0,5]],[1,[[1,6],[6,2]]]]
            [[[7,8],[0,6]],[0,[[2,9],[1,5]]]]
            [1,[[[4,4],1],[[3,2],[2,5]]]]
            [[[[9,8],[2,4]],[1,2]],[[[5,1],9],[[0,8],[5,2]]]]
            [[8,[[2,6],[4,6]]],[[0,[2,9]],[[2,2],[7,2]]]]
            [[[7,[8,1]],[[8,8],7]],[3,[7,[7,9]]]]
            [[6,[[3,1],[3,6]]],[[[5,8],[9,8]],[2,[7,4]]]]
            [[[4,[2,0]],[3,[3,3]]],[[6,[8,5]],5]]
            [[[3,2],3],[[8,2],8]]
            [[7,[[8,7],[5,8]]],[[2,0],[7,7]]]
            [[[[3,3],1],[[5,1],4]],[[4,3],[[4,9],8]]]
            [[[0,[5,8]],7],[4,9]]
            [[0,[[7,7],[1,1]]],[[0,[5,0]],[4,5]]]
            [[[[2,8],[1,6]],[[7,3],9]],[[2,8],[6,2]]]
            [[1,[4,7]],[8,0]]
            [3,[[[6,1],9],[[1,1],5]]]
            [[[[3,0],[9,8]],[6,[8,3]]],3]
            [4,[[1,[8,1]],[[6,0],2]]]
            [[4,[4,[0,3]]],[[[7,5],[0,2]],[[9,7],[6,5]]]]
            [[0,[4,[6,1]]],[[[1,9],[6,0]],9]]
            [[[[0,2],[8,4]],[2,3]],[[9,[8,4]],1]]
            [[[[1,2],[7,7]],[[3,8],3]],[[[1,1],[7,5]],6]]
            [[[[1,8],[8,4]],[[4,0],1]],[0,[1,[9,4]]]]
            [[[3,1],[9,5]],[[[9,5],4],[[8,7],4]]]
            [[[6,[3,0]],0],[[[6,9],7],[[6,1],[6,6]]]]
            [[[[9,6],[4,4]],5],9]
            [[5,[[6,0],0]],1]
            [3,[0,[4,[9,0]]]]
            [[[5,[2,2]],3],5]
            [[2,3],[9,[6,7]]]
            [[[[6,8],[7,9]],[4,7]],[[1,2],[0,1]]]""";
}
