package com.xb.cloud.disk.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Data
@ConfigurationProperties(prefix = "file")
@Component
public class RootDirectoryConfigProperties {

  /** 文件根目录 */
  private String rootDirectory;
}
