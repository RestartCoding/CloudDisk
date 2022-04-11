package com.xb.cloud.disk.core;

import java.util.Random;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public interface VerifyCodeSender {

  Random RANDOM = new Random();

  /** @param principle 手机号或邮箱号 */
  void send(String principle);

  default String buildVerifyCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      sb.append(RANDOM.nextInt(10));
    }
    return sb.toString();
  }
}
