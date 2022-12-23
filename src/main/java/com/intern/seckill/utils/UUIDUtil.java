package com.intern.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UUIDUtil {

   public static String uuid() {
      return UUID.randomUUID().toString().replace("-", "");
   }

   public static void main(String[] args) {
      System.out.println(uuid());
   }

}