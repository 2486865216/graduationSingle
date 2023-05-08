package com.example.graduationprojectsingle.config;

import com.alibaba.fastjson.JSONObject;
import com.example.graduationprojectsingle.utils.loginUtils.TokenUtil;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("filter初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 解决跨域的问题
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:10086");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,token,Authorization");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ("OPTIONS".equals(request.getMethod())) {
            map.put("code", 200);
        } else {
            String url = request.getRequestURI();
            if (url != null) {
                if (url.contains("favicon.ico")) {
                    return;
                }
                // 登录请求直接放行
                if ("/login/passwordLogin".equals(url)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                } else {
                    // 其他请求验证token
                    try {
                        String token = request.getHeader("token");
                        if (StringUtils.isNotEmpty(token)) {
                            // token验证结果
                            boolean verify = TokenUtil.tokenIsValid(token);
                            if (!verify) {
                                // 验证失败
                                map.put("code", 505);
                                map.put("msg", "token已过期");
                            } else {
                                // 验证成功，放行
                                filterChain.doFilter(servletRequest, servletResponse);
                                return;
                            }
                        } else {
                            // token为空的返回
                            map.put("code", 505);
                            map.put("msg", "未携带token信息");
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        map.put("code", 505);
                        map.put("msg", "验证身份失败");
                    }
                }
            }
            map.put("data", "验证身份失败");
        }
        response.setCharacterEncoding("utf-8");
        // 响应
        response.getWriter().write(JSONObject.toJSONString(map));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
