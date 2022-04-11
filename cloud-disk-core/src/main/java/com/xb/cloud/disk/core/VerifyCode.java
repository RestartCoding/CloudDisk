package com.xb.cloud.disk.core;

import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Data
public class VerifyCode {

  /** 验证码 */
  private String code;

  /** 过期时间 */
  private Long expired;
}
