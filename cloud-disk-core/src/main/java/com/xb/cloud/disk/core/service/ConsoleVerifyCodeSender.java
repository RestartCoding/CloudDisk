package com.xb.cloud.disk.core.service;

import com.xb.cloud.disk.core.VerifyCodeManager;
import com.xb.cloud.disk.core.VerifyCodeSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Component
@Primary
public class ConsoleVerifyCodeSender implements VerifyCodeSender {

  private static final Logger logger = LoggerFactory.getLogger(ConsoleVerifyCodeSender.class);

  @Override
  public void send(String principle) {
    String code = VerifyCodeManager.create(principle);
    if (logger.isDebugEnabled()) {
      logger.debug("Principle {} verify code is {}", principle, code);
    }
  }
}
