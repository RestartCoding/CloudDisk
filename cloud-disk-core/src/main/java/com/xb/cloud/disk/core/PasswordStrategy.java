package com.xb.cloud.disk.core;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public interface PasswordStrategy {

  /**
   * 处理密码
   *
   * @param password password
   * @return 处理之后的密码
   */
  String handle(String password);
}
