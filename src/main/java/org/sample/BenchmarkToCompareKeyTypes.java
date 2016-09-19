package org.sample;

import net.openhft.chronicle.map.ChronicleMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Singeev on 13.09.2016.
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkToCompareKeyTypes {

    private static final String VALUE = "longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongvalue";

    private static final byte[] AVERAGE_BYTE_ARRAY_KEY = ByteBuffer.allocate(12).putInt(0, 2102).putInt(4, 256987).putInt(8, 25).array();

//    @Param({"10000", "100000", "1000000"})
    @Param({"1000000"})
    public int elementsNumber;

    Random random = new Random();

    private ChronicleMap<String, String> map1mBigStringKey;
    private ChronicleMap<byte[], String> map1mByteArrayKey;

    @Setup
    public void setup(BenchmarkParams params) {
        System.out.println();
        System.out.println("Elements: " + elementsNumber);

        map1mBigStringKey = ChronicleMap
                .of(String.class, String.class)
                .averageKey("pid15648:cid1298754:ct34:lid1")
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber; i++) {
                String key = "pid" + i + ":cid" + i + 1 + ":ct" + i + 2 + ":lid" + i + 3;
                String value = VALUE + i;
                map1mBigStringKey.put(key, value);
        }
        System.out.println("Filled CronicleMap with " + map1mBigStringKey.size() + " elements (String key).");

        map1mByteArrayKey = ChronicleMap
                .of(byte[].class, String.class)
                .averageKey(AVERAGE_BYTE_ARRAY_KEY)
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber; i++) {
                byte[] key = ByteBuffer.allocate(12).putInt(0, i).putInt(4, i + 1).putInt(8, i + 2).array();
                String value = VALUE + i;
                map1mByteArrayKey.put(key, value);
        }
        System.out.println("Filled CronicleMap with " + map1mByteArrayKey.size() + " elements (byte[] key).");
    }

//    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithFullStringKey() {
        HashMap<String, String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            String key = "pid" + i + ":cid" + i + 1 + ":ct" + i + 2 + ":lid" + i + 3;
            result.put(key, map1mBigStringKey.get(key));
        }
        return result;
    }

//    @Benchmark
    @Fork(1)
    public HashMap<byte[], String> testGet1000ElementsWithFullByteArrayKey() {
        HashMap<byte[], String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            byte[] key = ByteBuffer.allocate(12).putInt(0, i).putInt(4, i + 1).putInt(8, i + 2).array();
            result.put(key, map1mByteArrayKey.get(key));
        }
        return result;
    }

    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithPartStringKey() {
        HashMap<String, String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            for(String key : map1mBigStringKey.keySet()){
                if(key.contains("pid" + i)){
                    result.put(key, map1mBigStringKey.get(key));
                }
            }
        }
        return result;
    }

//    @Benchmark
    @Fork(1)
    public HashMap<byte[], String> testGet1000ElementsWithPartByteArrayKey() {
        HashMap<byte[], String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            for(byte[] key : map1mByteArrayKey.keySet()){
                if(ByteBuffer.wrap(key).getInt(0) == i){
                    result.put(key, map1mByteArrayKey.get(key));
                }
            }
        }
        return result;
    }

    @Benchmark
    @Fork(1)
    public HashMap<byte[], String> testGet1000ElementsWithPartByteArrayKeyStream() {
        HashMap<byte[], String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            int finalI = i;
            map1mByteArrayKey.entrySet()
                    .stream()
                    .filter(e -> ByteBuffer.wrap(e.getKey()).getInt(0) == finalI)
                    .forEach(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }
}
