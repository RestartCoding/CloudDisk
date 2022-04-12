package com.xb.cloud.disk.core;

import com.xb.cloud.disk.core.service.ConsoleVerifyCodeSender;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public enum VerifyCodeType {
  /** TODO: 短信验证码 */
  PHONE(0, ConsoleVerifyCodeSender.class),

  /** TODO: 邮箱验证码 */
  EMAIL(1, ConsoleVerifyCodeSender.class),

  CONSOLE(2, ConsoleVerifyCodeSender.class);

  private final int code;

  private Class<? extends VerifyCodeSender> senderClass;

  VerifyCodeType(int code, Class<? extends VerifyCodeSender> senderClass) {
    this.code = code;
    this.senderClass = senderClass;
  }

  public int getCode() {
    return code;
  }

  public Class<? extends VerifyCodeSender> getSenderClass() {
    return senderClass;
  }
}
