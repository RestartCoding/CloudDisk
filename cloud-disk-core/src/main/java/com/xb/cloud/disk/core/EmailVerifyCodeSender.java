package com.xb.cloud.disk.core;

import java.util.Date;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Component
@AllArgsConstructor
public class EmailVerifyCodeSender implements VerifyCodeSender {

  private static final Logger logger = LoggerFactory.getLogger(EmailVerifyCodeSender.class);

  private MailSender mailSender;

  private MailProperties mailProperties;

  @Override
  public void send(String principle) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(mailProperties.getUsername());
    message.setSubject("Cloud disk verify code.");
    message.setTo(principle);
    message.setSentDate(new Date());
    message.setText(VerifyCodeMessageTemplate.format(VerifyCodeManager.create(principle)));
    try {
      mailSender.send(message);
    } catch (Exception e) {
      logger.error("Send email verify code failure", e);
      throw e;
    }
  }
}
