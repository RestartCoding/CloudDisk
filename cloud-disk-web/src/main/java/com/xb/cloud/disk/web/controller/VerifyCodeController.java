package com.xb.cloud.disk.web.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.xb.cloud.disk.core.VerifyCodeSender;
import com.xb.cloud.disk.core.VerifyCodeType;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
public class VerifyCodeController {

  @Resource private VerifyCodeSender verifyCodeSender;

  private Map<String, RateLimiter> verifyCodeLimiterMap = new ConcurrentHashMap<>();

  @GetMapping("/send")
  public void sendVerifyCode(
      @RequestParam VerifyCodeType type,
      @RequestParam String principle,
      HttpServletResponse response)
      throws IOException {
    RateLimiter rateLimiter = this.verifyCodeLimiterMap.get(principle);
    if (rateLimiter == null) {
      rateLimiter = RateLimiter.create(1 / 60D);
      verifyCodeLimiterMap.put(principle, rateLimiter);
    }
    if (rateLimiter.tryAcquire()) {
      verifyCodeSender.send(principle);
    } else {
      response.sendError(520, "You can get verify code every 60 seconds.");
    }
  }
}
