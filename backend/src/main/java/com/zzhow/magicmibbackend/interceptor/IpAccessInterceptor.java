package com.zzhow.magicmibbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * IP访问控制拦截器
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
@Component
public class IpAccessInterceptor implements HandlerInterceptor {

    // 是否允许局域网访问
    private static boolean allowLanAccess = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (allowLanAccess) {
            return true; // 允许所有访问
        }

        String clientIp = getClientIp(request);

        // 检查是否为本地地址
        if (isLocalAddress(clientIp)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"访问被拒绝：仅允许本地访问\"}");
        return false;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果是多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 检查是否为本地地址
     */
    private boolean isLocalAddress(String ip) {
        return ip.startsWith("127.") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("localhost");
    }

    /**
     * 设置是否允许局域网访问
     */
    public static void setAllowLanAccess(boolean allowLanAccess) {
        IpAccessInterceptor.allowLanAccess = allowLanAccess;
    }
}