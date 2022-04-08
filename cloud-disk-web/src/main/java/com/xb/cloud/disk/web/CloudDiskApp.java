package com.xb.cloud.disk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.xb.cloud.disk"})
public class CloudDiskApp {

  public static void main(String[] args) {
    SpringApplication.run(CloudDiskApp.class, args);
  }
}
