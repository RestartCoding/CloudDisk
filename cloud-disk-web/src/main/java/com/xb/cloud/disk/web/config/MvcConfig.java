package com.xb.cloud.disk.web.config;

import com.xb.cloud.disk.web.formatter.VerifyCodeTypeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new VerifyCodeTypeFormatter());
  }
}
