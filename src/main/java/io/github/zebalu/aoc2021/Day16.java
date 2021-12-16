package io.github.zebalu.aoc2021;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day16 {
    public static void main(String[] args) {
        String bitBuilder = toBits(INPUT);
        Packet packet = readPacket(bitBuilder.toString());
        firstPart(packet);
        secondPart(packet);
    }

    private static void firstPart(Packet packet) {
        System.out.println(packet.sumVersions());
    }

    private static void secondPart(Packet packet) {
        System.out.println(packet.value());
    }

    private static Packet readPacket(String string) {
        List<Packet> collector = new ArrayList<>();
        readSubPacket(string, collector);
        return collector.get(0);
    }

    private static int readSubPacket(String bitBuilder, List<Packet> collector) {
        int read = 0;
        int version = Integer.parseInt(bitBuilder.substring(read, read + 3), 2);
        read += 3;
        int typeId = Integer.parseInt(bitBuilder.substring(read, read + 3), 2);
        read += 3;
        if (typeId == 4) {
            StringBuilder subPacket = new StringBuilder();
            char bit = '1';
            do {
                bit = bitBuilder.charAt(read);
                subPacket.append(bitBuilder.substring(read + 1, read + 5));
                read += 5;
            } while (bit == '1');
            collector.add(new LiteralValue(version, typeId, Long.parseLong(subPacket.toString(), 2)));
            return read;
        } else {
            char lengthTypeId = bitBuilder.charAt(read);
            ++read;
            List<Packet> subpackets = new ArrayList<>();
            if ('1' == lengthTypeId) {
                int numberOfSubPackets = Integer.parseInt(bitBuilder.substring(read, read + 11), 2);
                read += 11;
                for (int p = 0; p < numberOfSubPackets; ++p) {
                    read += readSubPacket(bitBuilder.substring(read), subpackets);
                }
                collector.add(new Operator(version, typeId, subpackets));
                return read;
            } else {
                int numberOfSubPacketBites = Integer.parseInt(bitBuilder.substring(read, read + 15), 2);
                read += 15;
                String bits = bitBuilder.substring(read, read + numberOfSubPacketBites);
                int readBits = 0;
                while (readBits < numberOfSubPacketBites) {
                    int consumed = readSubPacket(bits, subpackets);
                    readBits += consumed;
                    bits = bits.substring(consumed);
                }
                read += numberOfSubPacketBites;
                collector.add(new Operator(version, typeId, subpackets));
                return read;
            }
        }
    }

    private static String toBits(String str) {
        StringBuilder bitBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            bitBuilder.append(String.format("%04d",
                    new BigInteger(Integer.toBinaryString(Integer.parseInt(str.substring(i, i + 1), 16)))));
        }
        return bitBuilder.toString();
    }

    private static abstract sealed class Packet permits LiteralValue, Operator {
        protected final int version;
        protected final int typeId;

        Packet(int version, int typeId) {
            this.version = version;
            this.typeId = typeId;
        }

        abstract int sumVersions();

        abstract long value();
    }

    private static final class LiteralValue extends Packet {
        protected final long value;

        public LiteralValue(int version, int typeId, long value) {
            super(version, typeId);
            this.value = value;
        }

        @Override
        int sumVersions() {
            return version;
        }

        @Override
        long value() {
            return value;
        }
    }

    private static final class Operator extends Packet {
        protected final List<Packet> subPackets;

        public Operator(int version, int typeId, List<Packet> subPackets) {
            super(version, typeId);
            this.subPackets = subPackets;
        }

        @Override
        int sumVersions() {
            return subPackets.stream().mapToInt(Packet::sumVersions).sum() + version;
        }

        @Override
        long value() {
            return switch (typeId) {
            case 0 -> subPackets.stream().mapToLong(Packet::value).sum();
            case 1 -> subPackets.stream().mapToLong(Packet::value).reduce(1L, (a, v) -> a * v);
            case 2 -> subPackets.stream().mapToLong(Packet::value).min().orElseThrow();
            case 3 -> subPackets.stream().mapToLong(Packet::value).max().orElseThrow();
            case 5 -> subPackets.get(0).value() > subPackets.get(1).value() ? 1L : 0L;
            case 6 -> subPackets.get(0).value() < subPackets.get(1).value() ? 1L : 0L;
            case 7 -> subPackets.get(0).value() == subPackets.get(1).value() ? 1L : 0L;
            default -> throw new IllegalStateException("unknown type: " + typeId);
            };
        }
    }

    private static final String INPUT = """
            620D79802F60098803B10E20C3C1007A2EC4C84136F0600BCB8AD0066E200CC7D89D0C4401F87104E094FEA82B0726613C6B692400E14A305802D112239802125FB69FF0015095B9D4ADCEE5B6782005301762200628012E006B80162007B01060A0051801E200528014002A118016802003801E2006100460400C1A001AB3DED1A00063D0E25771189394253A6B2671908020394359B6799529E69600A6A6EB5C2D4C4D764F7F8263805531AA5FE8D3AE33BEC6AB148968D7BFEF2FBD204CA3980250A3C01591EF94E5FF6A2698027A0094599AA471F299EA4FBC9E47277149C35C88E4E3B30043B315B675B6B9FBCCEC0017991D690A5A412E011CA8BC08979FD665298B6445402F97089792D48CF589E00A56FFFDA3EF12CBD24FA200C9002190AE3AC293007A0A41784A600C42485F0E6089805D0CE517E3C493DC900180213D1C5F1988D6802D346F33C840A0804CB9FE1CE006E6000844528570A40010E86B09A32200107321A20164F66BAB5244929AD0FCBC65AF3B4893C9D7C46401A64BA4E00437232D6774D6DEA51CE4DA88041DF0042467DCD28B133BE73C733D8CD703EE005CADF7D15200F32C0129EC4E7EB4605D28A52F2C762BEA010C8B94239AAF3C5523CB271802F3CB12EAC0002FC6B8F2600ACBD15780337939531EAD32B5272A63D5A657880353B005A73744F97D3F4AE277A7DA8803C4989DDBA802459D82BCF7E5CC5ED6242013427A167FC00D500010F8F119A1A8803F0C62DC7D200CAA7E1BC40C7401794C766BB3C58A00845691ADEF875894400C0CFA7CD86CF8F98027600ACA12495BF6FFEF20691ADE96692013E27A3DE197802E00085C6E8F30600010882B18A25880352D6D5712AE97E194E4F71D279803000084C688A71F440188FB0FA2A8803D0AE31C1D200DE25F3AAC7F1BA35802B3BE6D9DF369802F1CB401393F2249F918800829A1B40088A54F25330B134950E0""";
}
