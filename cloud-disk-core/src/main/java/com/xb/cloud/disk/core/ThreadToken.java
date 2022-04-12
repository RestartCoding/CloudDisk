package com.xb.cloud.disk.core;

import java.lang.ref.SoftReference;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
public class ThreadToken {

  @SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
  private static final ThreadLocal<SoftReference<String>> threadToken =
      new InheritableThreadLocal<>();

  private ThreadToken() {}

  public static void set(String token) {
    threadToken.set(new SoftReference<>(token));
  }

  public static String get() {
    return threadToken.get().get();
  }

  public static void remove() {
    threadToken.remove();
  }
}
