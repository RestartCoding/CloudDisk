package com.xb.cloud.disk.web.controller;

import com.xb.cloud.disk.core.VerifyCodeSender;
import com.xb.cloud.disk.core.VerifyCodeType;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@RestController
@RequestMapping("/verifyCode")
@AllArgsConstructor
public class VerifyCodeController {

  private VerifyCodeSender verifyCodeSender;

  @GetMapping("/send")
  public void sendVerifyCode(@RequestParam VerifyCodeType type, @RequestParam String principle) {
    verifyCodeSender.send(principle);
  }
}
