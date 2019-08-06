package com.liushimin.test;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;

public class Test1 {
    public static void main(String[] args) {
        String str = "1";
        String encodeStr = DigestUtils.md5DigestAsHex(str.getBytes());
        System.out.println(encodeStr);
    }
}
