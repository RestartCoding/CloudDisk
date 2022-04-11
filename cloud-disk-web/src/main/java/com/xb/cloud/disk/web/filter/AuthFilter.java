package com.xb.cloud.disk.web.filter;

import com.xb.cloud.disk.core.TokenManager;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@WebFilter(urlPatterns = {"/**"})
@Component
public class AuthFilter implements Filter {

  private Set<String> ignorePath = Set.of("/user/login", "/user/registerByPhone", "/user/registerByEmail", "/verifyCode/send");

  @Resource private TokenManager tokenManager;

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    String uri = httpServletRequest.getRequestURI();
    if (ignorePath.contains(uri)) {
      filterChain.doFilter(servletRequest, servletResponse);
    } else {
      String authorization = httpServletRequest.getHeader("Authorization");
      if (StringUtils.hasText(authorization) && tokenManager.load(authorization) != null) {
        filterChain.doFilter(servletRequest, servletResponse);
      } else {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        sendUnauthorizedResponse(response);
      }
    }
  }

  private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
    response.sendError(401, "Unauthorized");
  }
}
