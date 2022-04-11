package com.xb.cloud.disk.core;

import com.xb.cloud.disk.core.entity.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Component
public class LocalTokenManager implements TokenManager {

  private final Map<String, User> tokenCache;

  private final Map<String, String> usernameToToken;

  public LocalTokenManager() {
    tokenCache = new ConcurrentHashMap<>();
    usernameToToken = new ConcurrentHashMap<>();
  }

  @Override
  public User load(String token) {
    return tokenCache.get(token);
  }

  @Override
  public String store(User user) {
    Assert.notNull(user, "User can not be null.");
    String token = TokenGenerator.generate();
    if (usernameToToken.containsKey(user.getUsername())) {
      return token;
    }
    tokenCache.put(token, user);
    usernameToToken.put(user.getUsername(), token);
    return token;
  }

  @Override
  public void remove(String token) {
    User user = tokenCache.remove(token);
    if (user != null) {
      usernameToToken.remove(user.getUsername());
    }
  }
}
