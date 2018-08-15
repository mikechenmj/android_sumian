package com.sumian.sd.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.junit.Test;

import java.lang.reflect.Type;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 21:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JsonUtilTest {

    @Test
    public void test() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
        builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
        Gson gson = builder.create();
        String json = "{\"enable\":1, \"age\":0}";
        ClassA a = gson.fromJson(json, ClassA.class);
        System.out.println(a);
    }

    static class  ClassA {
        boolean enable;
        int age;

        @Override
        public String toString() {
            return "ClassA{" +
                    "enable=" + enable +
                    ", age=" + age +
                    '}';
        }
    }

    class BooleanTypeAdapter implements JsonDeserializer<Boolean>
    {
        public Boolean deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException
        {
            int code = json.getAsInt();
            return code == 1;
        }
    }
}
