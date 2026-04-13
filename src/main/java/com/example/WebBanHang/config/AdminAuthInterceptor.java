package com.example.WebBanHang.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.WebBanHang.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (currentUser == null ||
            (!"ADMIN".equals(currentUser.getRole()) && !"STAFF".equals(currentUser.getRole()))) {

            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("application/json")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                    "{\"status\":\"ERROR\",\"message\":\"Bạn chưa đăng nhập hoặc không có quyền truy cập!\",\"data\":null}"
                );
            } else {
                response.sendRedirect("/login?error=unauthorized");
            }
            return false;
        }
        return true;
    }
}