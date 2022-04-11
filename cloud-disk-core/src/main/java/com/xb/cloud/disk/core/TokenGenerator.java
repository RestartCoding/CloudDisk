package com.xb.cloud.disk.core;

import java.util.UUID;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public class TokenGenerator {

  private static final UUID generator = UUID.randomUUID();

  public static String generate() {
    return generator.toString();
  }
}
