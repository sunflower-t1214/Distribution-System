package com.example.server.config;

import com.example.server.common.JwtUtils;
import com.example.server.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();

        if (uri.contains("/user/login") || uri.contains("/user/register") ||
            uri.contains("/auth/code") || uri.contains("/auth/verify") ||
            uri.contains("/products/list") || uri.contains("/products/page") ||
            uri.contains("/products/categories") || uri.contains("/data/pipeline")) {
            return true;
        }

        if (uri.matches(".*/products/\\d+$")) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token已过期\"}");
            return false;
        }

        Integer userId = JwtUtils.parseUserId(auth.substring(7));
        if (userId == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效，请重新登录\"}");
            return false;
        }

        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
