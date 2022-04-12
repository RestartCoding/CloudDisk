package com.xb.cloud.disk.web.dto.user;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Data
public class LoginDTO {

  @NotEmpty(message = "Username can not be empty.")
  private String username;

  @NotEmpty(message = "Password can not be empty.")
  private String password;
}
