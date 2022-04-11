package com.xb.cloud.disk.core;

import com.xb.cloud.disk.core.entity.User;
import org.springframework.lang.Nullable;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
public interface TokenManager {

  /**
   * get user by token
   *
   * @param token token
   * @return user. maybe null.
   */
  @Nullable
  User load(String token);

  /**
   * store token
   *
   * @param user user
   * @return token
   */
  String store(User user);

  /**
   * remove token
   *
   * @param token token
   */
  void remove(String token);
}
