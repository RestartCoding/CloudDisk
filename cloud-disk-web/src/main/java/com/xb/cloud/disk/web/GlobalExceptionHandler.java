package com.xb.cloud.disk.web;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public String defaultHandle(Exception e) {
    return e.getMessage();
  }
}
