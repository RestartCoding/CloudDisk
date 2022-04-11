package com.xb.cloud.disk.core;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Component
@Primary
public class DefaultPasswordStrategy implements PasswordStrategy {

  @Override
  public String handle(String password) {
    return DigestUtils.md5DigestAsHex(password.getBytes());
  }
}
