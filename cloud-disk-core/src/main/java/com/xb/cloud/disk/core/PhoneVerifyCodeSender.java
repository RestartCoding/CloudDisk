package com.xb.cloud.disk.core;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public class PhoneVerifyCodeSender implements VerifyCodeSender {

  @Override
  public void send(String principle) {
    throw new UnsupportedOperationException("Sending phone verify code is unsupported.");
  }
}
