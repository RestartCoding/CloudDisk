package com.xb.cloud.disk.web.formatter;

import com.google.common.base.Enums;
import com.xb.cloud.disk.core.VerifyCodeType;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
public class VerifyCodeTypeFormatter implements Formatter<VerifyCodeType> {

  @Override
  public VerifyCodeType parse(String s, Locale locale) throws ParseException {
    return Enums.getIfPresent(VerifyCodeType.class, s)
        .or(
            () -> {
              int code = Integer.parseInt(s);
              for (VerifyCodeType type : VerifyCodeType.values()) {
                if (type.getCode() == code) {
                  return type;
                }
              }
              return null;
            });
  }

  @Override
  public String print(VerifyCodeType type, Locale locale) {
    return type.toString();
  }
}
