/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import net.openhft.chronicle.map.ChronicleMap;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkWrite {

    private static final String KEY = "keykeykeykeykeykeykeykeykeykeykeykeykeykeykey";
    private static final String VALUE = "longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglongvalue";

    @Param({"1000", "100000", "1000000"})
    public int elementsNumber = 1000;

    @Benchmark
    @Fork(1)
    public void testFillUpChronicle() {
        ChronicleMap<String, String> allSystemTexts = ChronicleMap
                .of(String.class, String.class)
                .averageKey("1:client_menu_special_offer.operation_type.MANUAL_CHANGE")
                .averageValue(VALUE)
                .entries(elementsNumber)
                .create();

        for (int i = 0; i < elementsNumber; i++) {
            String key = KEY + i;
            String value = VALUE + i;
            allSystemTexts.put(key, value);
        }

        allSystemTexts.close();
    }

    @Benchmark
    @Fork(1)
    public HashMap testHashMap() {
        HashMap<String, String> allSystemTexts = new HashMap<>(elementsNumber);

        for (int i = 0; i < elementsNumber; i++) {
            String key = KEY + i;
            String value = VALUE + i;
            allSystemTexts.put(key, value);
        }

        return allSystemTexts;
    }

//    private static String averageValueForSystemTexts = "Добрый день!<br/>\n" +
//            "<br/>\n" +
//            "Вам выслан счет за использование мобильной программы лояльности за ${month}.<br/>\n" +
//            "<br/>\n" +
//            "Счет необходимо оплатить до ${endDate}.<br/>\n" +
//            "<br/>\n" +
//            "Это письмо сгенерировано автоматически, пожалуйста, не отвечайте на него. По всем вопросам, связанным со счетами, Вы можете обратиться к своему менеджеру (${managerName}).<br/>\n" +
//            "<br/>\n" +
//            "Если у Вас не открывается прикрепленный файл со счетом, пожалуйста, скачайте его по ссылке:\n" +
//            "${downloadLink}<br/>\n" +
//            "<br/>\n" +
//            "____________________<br/>\n" +
//            "С уважением, <br/>\n"+
//            "компания “Фабрика лояльности”\n" +
//            "8 800 333 21 33";

//    private static byte[] averageKeyForContentText = ByteBuffer.allocate(12).putInt(0, 2102).putInt(4, 256987).putInt(8, 25).array();
//
//    private static String averageValueForContentTexts = "Расскажите Вашим друзьям о том, что в ресторане Clumba Club можно получить подарок с помощью приложения.\n" +
//            "1. Получите 10 баллов, если Ваш друг установит приложение Clumba Club по Вашей ссылке.\n" +
//            "2. Получите 50 баллов, когда он получит свой первый подарок.\n" +
//            "Разместить ссылку на Вашей стене ВКонтакте?\n" +
//            "(текст сообщения Вы сможете отредактировать)";
//
    //create ChronicalMap for system texts
//    private ChronicleMap<String, String> allSystemTexts = ChronicleMap
//            .of(String.class, String.class)
//            .averageKey("1:client_menu_special_offer.operation_type.MANUAL_CHANGE")
//            .averageValue(averageValueForSystemTexts)
//            .entries(1000L)
//            .create();
//
//    private ChronicleMap<String, String> allSystemTexts100k = ChronicleMap
//            .of(String.class, String.class)
//            .averageKey("1:client_menu_special_offer.operation_type.MANUAL_CHANGE")
//            .averageValue(averageValueForSystemTexts)
//            .entries(100000L)
//            .create();
//
//    private ChronicleMap<String, String> allSystemTexts10m = ChronicleMap
//            .of(String.class, String.class)
//            .averageKey("1:client_menu_special_offer.operation_type.MANUAL_CHANGE")
//            .averageValue(averageValueForSystemTexts)
//            .entries(10000000L)
//            .create();
//
//    //create ChronicalMap for content texts
//    private static ChronicleMap<byte[], String> allContentTexts = ChronicleMap
//            .of(byte[].class, String.class)
//            .averageKey(averageKeyForContentText)
//            .averageValue(averageValueForContentTexts)
//            .entries(10000L)
//            .create();

//    private static Random r = new Random();

//    private String randomString(final int length) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            char c = (char) (r.nextInt((int) (Character.MAX_VALUE)));
//            sb.append(c);
//        }
//        return sb.toString();
//    }


}
