package org.sample;

import net.openhft.chronicle.map.ChronicleMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.io.Serializable;
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

    private static final byte[] AVERAGE_BYTE_ARRAY_KEY = ByteBuffer.allocate(12).putInt(0, 2102).putInt(4, 256987).putInt(8, 25).array();
    private static final ContentTextKey AVERAGE_OBJ_KEY = new ContentTextKey(256987, 25, 1);
    private static final String VALUE = "longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongvalue";
    private static int randomIndex;

    @Param({"1000000"})
    public int elementsNumber;

    Random random = new Random();

    private ChronicleMap<byte[], String> map1mByteArrayKey;
    private ChronicleMap<String, String> map1mBigStringKey;
    private ChronicleMap<ContentTextKey, String> map1mObjKey;

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

        map1mObjKey = ChronicleMap
                .of(ContentTextKey.class, String.class)
                .averageKey(AVERAGE_OBJ_KEY)
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber; i++) {
            ContentTextKey key = new ContentTextKey(i, i + 1, i + 2);
            String value = VALUE + i;
            map1mObjKey.put(key, value);
        }
        System.out.println("Filled CronicleMap with " + map1mObjKey.size() + " elements (Obj key).");

        randomIndex = random.nextInt();
    }

    /*--------------------------------------------------------------
      бенчмарки для выборки 1000 элементов из мэпы по ПОЛНОМУ ключу
     --------------------------------------------------------------*/

    @Benchmark
    @Fork(1)
    public HashMap<String, String> testGet1000ElementsWithFullStringKey() {
        HashMap<String, String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            String key = "pid" + i + ":cid" + i + 1 + ":ct" + i + 2 + ":lid" + i + 3;
            result.put(key, map1mBigStringKey.get(key));
        }
        return result;
    }

    @Benchmark
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
    public HashMap<ContentTextKey, String> testGet1000ElementsWithObjKey() {
        HashMap<ContentTextKey, String> result = new HashMap<>();
        for(int i = 0; i < 1000; i++) {
            ContentTextKey key = new ContentTextKey(i, i + 1, i + 2);
            result.put(key, map1mObjKey.get(key));
        }
        return result;
    }

    /*--------------------------------------------------------------
      бенчмарки для выборки 1 элемента из мэпы по НЕ полному ключу
     --------------------------------------------------------------*/

    @Benchmark
    @Fork(1)
    public String testGet1000ElementsWithPartStringKey() {
       String result = null;
            for(String key : map1mBigStringKey.keySet()){
                if(key.contains("pid" + randomIndex)){
                    result = map1mBigStringKey.get(key);
                }
            }
        return result;
    }

    @Benchmark
    @Fork(1)
    public String testGet1000ElementsWithPartByteArrayKey() {
        String result = null;
        for (byte[] key : map1mByteArrayKey.keySet()) {
            if (ByteBuffer.wrap(key).getInt(0) == randomIndex) {
                result = map1mByteArrayKey.get(key);
            }
        }
        return result;
    }

    @Benchmark
    @Fork(1)
    public String testGet1000ElementsWithPartObjKey() {
        String result = null;
        for (ContentTextKey key : map1mObjKey.keySet()) {
            if (key.getComponentId() == randomIndex) {
                result = map1mObjKey.get(key);
            }
        }
        return result;
    }
}

class ContentTextKey implements Serializable {
    int componentId;
    int componentType;
    int languegeId;

    public ContentTextKey(int componentId, int componentType, int languegeId) {
        this.componentId = componentId;
        this.componentType = componentType;
        this.languegeId = languegeId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentType() {
        return componentType;
    }

    public void setComponentType(int componentType) {
        this.componentType = componentType;
    }

    public int getLanguegeId() {
        return languegeId;
    }

    public void setLanguegeId(int languegeId) {
        this.languegeId = languegeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentTextKey)) return false;

        ContentTextKey that = (ContentTextKey) o;

        if (componentId != that.componentId) return false;
        if (componentType != that.componentType) return false;
        return languegeId == that.languegeId;

    }

    @Override
    public int hashCode() {
        int result = componentId;
        result = 31 * result + componentType;
        result = 31 * result + languegeId;
        return result;
    }
}
