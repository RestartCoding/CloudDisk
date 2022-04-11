package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.core.PasswordStrategy;
import com.xb.cloud.disk.core.VerifyCode;
import com.xb.cloud.disk.core.VerifyCodeManager;
import com.xb.cloud.disk.core.entity.User;
import com.xb.cloud.disk.core.mapper.UserMapper;
import com.xb.cloud.disk.core.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  private PasswordStrategy passwordStrategy;

  @Override
  public void register(User user, String principle, String verifyCode) {
    VerifyCode verifyCodeObj = VerifyCodeManager.get(principle);
    if (verifyCodeObj != null
        && verifyCodeObj.getCode().equals(verifyCode)
        && verifyCodeObj.getExpired() > System.currentTimeMillis()) {
      String handledPassword = passwordStrategy.handle(user.getPassword());
      user.setPassword(handledPassword);
      save(user);
      // 移除验证码，一个验证码只能使用一次
      VerifyCodeManager.remove(principle);
      return;
    }
    throw new RuntimeException("Invalid verify code.");
  }
}
