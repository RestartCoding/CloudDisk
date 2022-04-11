package com.xb.cloud.disk.core;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public enum VerifyCodeType {
  /** 短信验证码 */
  PHONE(0),

  /** 邮箱验证码 */
  EMAIL(1);

  private final int code;

  VerifyCodeType(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
