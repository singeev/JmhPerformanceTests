package org.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.openhft.chronicle.map.ChronicleMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Singeev on 13.09.2016.
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkToCompareKeyTypes {

    private static final String KEY = "keykeykeykeykeykeykeykeykeykeykeykeykeykeykey";
    private static final String VALUE = "longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongvalue";

    private static final byte[] AVERAGE_BYTE_ARRAY_KEY = ByteBuffer.allocate(12).putInt(0, 2102).putInt(4, 256987).putInt(8, 25).array();

//    @Param({"10000", "100000", "1000000"})
    @Param({"1000000"})
    public int elementsNumber;

    Random random = new Random();

    private ChronicleMap<String, String> map1mStringKey;
    private ChronicleMap<String, String> map1mBigStringKey;
    private ChronicleMap<String, String> map1mBigStringKeyJSON;
    private ChronicleMap<byte[], String> map1mByteArrayKey;

    @Setup
    public void setup(BenchmarkParams params) {
        System.out.println();
        System.out.println("Elements: " + elementsNumber);

        map1mStringKey = ChronicleMap
                .of(String.class, String.class)
                .averageKey("15648:keykeykeykeykeykeykeykeykeykeykeykeykeykeykey")
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber/1000; i++) {
            for(int j = 0; j < 1000; j++) {//to make keys with duplicate value of a 1st parameter
                String key = j + ":" + KEY;
                String value = VALUE + i;
                map1mStringKey.put(key, value);
            }
        }

        String JSON = "{\"1\":\"textForLanguageId-1\",\"2\":\"textForLanguageId-2\",\"3\":\"textForLanguageId-3\",\"4\":\"textForLanguageId-4\",\"5\":\"textForLanguageId-5\",\"6\":\"textForLanguageId-6\",\"7\":\"textForLanguageId-7\",\"8\":\"textForLanguageId-8\",\"9\":\"textForLanguageId-9\",\"10\":\"textForLanguageId-10\",\"11\":\"textForLanguageId-11\",\"12\":\"textForLanguageId-12\"}";

        map1mBigStringKeyJSON = ChronicleMap
                .of(String.class, String.class)
                .averageKey("pid15648:cid1298754:ct34:lid1")
                .averageValue(JSON)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber/1000; i++) {
            for(int j = 0; j < 1000; j++) {//to make keys with duplicate value of a 1st parameter
                String key = "pid" + i + ":cid" + i + ":ct" + i + 1 + ":lid" + i + 1;
                String value = JSON;
                map1mBigStringKey.put(key, value);
            }
        }

        map1mBigStringKey = ChronicleMap
                .of(String.class, String.class)
                .averageKey("pid15648:cid1298754:ct34:lid1")
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber/1000; i++) {
            for(int j = 0; j < 1000; j++) {//to make keys with duplicate value of a 1st parameter
                String key = "pid" + i + ":cid" + i + ":ct" + i + 1 + ":lid" + i + 1;
                String value = VALUE + i;
                map1mBigStringKey.put(key, value);
            }
        }

        map1mByteArrayKey = ChronicleMap
                .of(byte[].class, String.class)
                .averageKey(AVERAGE_BYTE_ARRAY_KEY)
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber/1000; i++) {
            for(int j = 0; j < 1000; j++) {
                byte[] key = ByteBuffer.allocate(12).putInt(0, i).putInt(4, j).putInt(8, j + i).array(); //to make keys with duplicate value of a 2nd parameter
                String value = VALUE + i;
                map1mByteArrayKey.put(key, value);
            }
        }
    }

//    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithStringKey1() {
        HashMap<String, String> result = new HashMap<>();
        map1mStringKey.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(String.valueOf(random.nextInt(1000))))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

//    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithStringKey2() {
        HashMap<String, String> result = new HashMap<>();
        map1mStringKey.entrySet()
                .stream()
                .filter(e -> e.getKey().contains(String.valueOf(random.nextInt(1000))))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

//    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithBigStringKey() {
        HashMap<String, String> result = new HashMap<>();
        map1mStringKey.entrySet()
                .stream()
                .filter(e -> e.getKey().contains(String.valueOf("pid" + random.nextInt(1000))))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithFullStringKey() {
        HashMap<String, String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {//to make keys with duplicate value of a 1st parameter
            String key = "pid" + i + ":cid" + i + ":ct" + i + 1 + ":lid" + i + 1;
            result.put(key,map1mBigStringKey.get(key));
        }
        return result;
    }

    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithFullStringKeyJSON() {
        HashMap<String, String> result = new HashMap<>();
        int languageId = random.nextInt(12);
        for(int i = 0; i < 1000; i++) {
            String key = "pid" + i + ":cid" + i + ":ct" + i + 1 + ":lid" + i + 1;
            String jsonString = map1mBigStringKeyJSON.get(key);
            Map<Integer, String> mapFromJson = null;
            try {
                mapFromJson = new ObjectMapper().readValue(
                        jsonString, new TypeReference<HashMap<Integer, String>>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.put(key,mapFromJson.get(languageId));
        }
        return result;
    }

//    @Benchmark
    @Fork(1)
    public HashMap<byte[], String> testGet1000ElementsWithByteArrayKey() {
        HashMap<byte[], String> result = new HashMap<>();
        map1mByteArrayKey.entrySet()
                .stream()
                .filter(e -> ByteBuffer.wrap(e.getKey()).getInt(0) == random.nextInt(1000))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
