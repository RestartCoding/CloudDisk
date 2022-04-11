package com.xb.cloud.disk.core;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public class VerifyCodeManager {

  private static Map<String, VerifyCode> container = new ConcurrentHashMap<>();

  /** 存活时间 */
  private static final long TTL_MILLIS_SECONDS = 60 * 5 * 1000;

  private static Random random = new Random();

  private static final int LENGTH = 6;

  public static VerifyCode get(String principle) {
    return container.get(principle);
  }

  public static String create(String principle) {
    String code = buildVerifyCode();
    VerifyCode verifyCode = new VerifyCode();
    verifyCode.setCode(code);
    verifyCode.setExpired(System.currentTimeMillis() + TTL_MILLIS_SECONDS);
    container.put(principle, verifyCode);
    return code;
  }

  public static VerifyCode remove(String principle) {
    return container.remove(principle);
  }

  private static String buildVerifyCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < LENGTH; i++) {
      sb.append(random.nextInt(10));
    }
    return sb.toString();
  }
}
