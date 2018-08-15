package com.sumian.sd.doctor;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import org.junit.Test;

/**
 * Created by sm
 * on 2018/6/2 23:28
 * desc:
 **/
public class OrderTest {

    @Test
    public void testOrder() {

        String json = "{\n" +
                "  \"id\": \"ch_5ijPiHH8GenDyb5i58P4ebbP\",\n" +
                "  \"object\": \"charge\",\n" +
                "  \"created\": 1527951011,\n" +
                "  \"livemode\": false,\n" +
                "  \"paid\": false,\n" +
                "  \"refunded\": false,\n" +
                "  \"reversed\": false,\n" +
                "  \"app\": \"app_HqT0GKqzbnvH5Ou1\",\n" +
                "  \"channel\": \"wx\",\n" +
                "  \"order_no\": \"1806021527951011478\",\n" +
                "  \"client_ip\": \"119.123.73.152\",\n" +
                "  \"amount\": 1,\n" +
                "  \"amount_settle\": 1,\n" +
                "  \"currency\": \"cny\",\n" +
                "  \"subject\": \"图文咨询服务\",\n" +
                "  \"body\": \"图文咨询服务\",\n" +
                "  \"extra\": {},\n" +
                "  \"time_paid\": null,\n" +
                "  \"time_expire\": 1527958211,\n" +
                "  \"time_settle\": null,\n" +
                "  \"transaction_no\": null,\n" +
                "  \"refunds\": {\n" +
                "    \"object\": \"list\",\n" +
                "    \"url\": \"/v1/charges/ch_5ijPiHH8GenDyb5i58P4ebbP/refunds\",\n" +
                "    \"has_more\": false,\n" +
                "    \"data\": []\n" +
                "  },\n" +
                "  \"amount_refunded\": 0,\n" +
                "  \"failure_code\": null,\n" +
                "  \"failure_msg\": null,\n" +
                "  \"metadata\": {\n" +
                "    \"package_id\": 13,\n" +
                "    \"quantity\": 1\n" +
                "  },\n" +
                "  \"credential\": {\n" +
                "    \"object\": \"credential\",\n" +
                "    \"wx\": {\n" +
                "      \"appId\": \"wx4yrjvpxdih04v9ki\",\n" +
                "      \"partnerId\": \"3273792653\",\n" +
                "      \"prepayId\": \"1101000000180602tuh0o05efvvha9sq\",\n" +
                "      \"nonceStr\": \"7b1c4e2f880b019e53ba1dfaeeee7e59\",\n" +
                "      \"timeStamp\": 1527951011,\n" +
                "      \"packageValue\": \"Sign=WXPay\",\n" +
                "      \"sign\": \"98B2EF7D2784669D6D38C003122EE301\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"description\": null\n" +
                "}";


        String parseObject = JSON.parseObject(json, String.class);

        System.out.println("1    fromJson=" + parseObject);


        Gson gson = new Gson();
        String fromJson = gson.fromJson(json, String.class);
        System.out.println("2    fromJson=" + fromJson);


    }
}
