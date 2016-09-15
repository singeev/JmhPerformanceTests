package org.sample;

import net.openhft.chronicle.map.ChronicleMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by ilya on 13.09.16.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkRead {

    private static final String KEY = "keykeykeykeykeykeykeykeykeykeykeykeykeykeykey";
    private static final String VALUE = "longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongvalue";

    @Param({"1000", "100000", "1000000"})
    public int elementsNumber;

    Random random = new Random();

    private ChronicleMap<String, String> allSystemTexts10m;

    @Setup
    public void setup(BenchmarkParams params) {
        System.out.println();
        System.out.println("Elements: " + elementsNumber);

        allSystemTexts10m = ChronicleMap
                .of(String.class, String.class)
                .averageKey("1:client_menu_special_offer.operation_type.MANUAL_CHANGE")
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber; i++) {
            String key = KEY + i;
            String value = VALUE + i;
            allSystemTexts10m.put(key, value);
        }
    }

//    @Benchmark
    @Fork(1)
    public String testRead() {
        return allSystemTexts10m.get(KEY + random.nextInt(elementsNumber));
    }

}
