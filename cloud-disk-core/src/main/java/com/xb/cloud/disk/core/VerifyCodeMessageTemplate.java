package com.xb.cloud.disk.core;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public class VerifyCodeMessageTemplate {

  private static String template = "Hello, your cloud disk verify code is %s";

  public static String format(String verifyCode) {
    return String.format(template, verifyCode);
  }
}
