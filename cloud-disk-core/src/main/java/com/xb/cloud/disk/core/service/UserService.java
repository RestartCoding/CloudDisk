package com.xb.cloud.disk.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.cloud.disk.core.entity.User;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public interface UserService extends IService<User> {

  /**
   * 注册用户
   *
   * @param user user
   * @param principle principle
   * @param verifyCode verifyCode
   */
  void register(User user, String principle, String verifyCode);
}
