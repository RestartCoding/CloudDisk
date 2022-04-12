package com.xb.cloud.disk.web.controller;

import com.xb.cloud.disk.core.entity.User;
import com.xb.cloud.disk.core.service.UserService;
import com.xb.cloud.disk.web.dto.user.LoginDTO;
import com.xb.cloud.disk.web.dto.user.RegisterByEmailDTO;
import com.xb.cloud.disk.web.dto.user.RegisterByPhoneDTO;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private UserService userService;

  @PostMapping("/registerByPhone")
  public void registerByPhone(@RequestBody @Validated RegisterByPhoneDTO registerByPhoneDTO) {
    User user = new User();
    user.setUsername(registerByPhoneDTO.getUsername());
    user.setPassword(registerByPhoneDTO.getPassword());
    user.setPhone(registerByPhoneDTO.getPhone());

    userService.register(user, registerByPhoneDTO.getPhone(), registerByPhoneDTO.getVerifyCode());
  }

  @PostMapping("/registerByEmail")
  public void registerByEmail(@RequestBody @Validated RegisterByEmailDTO registerByEmailDTO) {
    User user = new User();
    user.setUsername(registerByEmailDTO.getUsername());
    user.setPassword(registerByEmailDTO.getPassword());
    user.setEmail(registerByEmailDTO.getEmail());

    userService.register(user, registerByEmailDTO.getEmail(), registerByEmailDTO.getVerifyCode());
  }

  @PostMapping("/login")
  public String login(@RequestBody @Validated LoginDTO loginDTO) {
    return userService.login(loginDTO.getUsername(), loginDTO.getPassword());
  }
}
