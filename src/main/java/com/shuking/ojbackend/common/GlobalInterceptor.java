package com.shuking.ojbackend.common;

import com.shuking.ojbackend.constant.UserConstant;
import com.shuking.ojbackend.exception.BusinessException;
import com.shuking.ojbackend.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class GlobalInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /*预检验请求放行
          CROS复杂请求时会首先发送一个OPTIONS请求做嗅探，来测试服务器是否支持本次请求，请求成功后才会发送真实的请求；
          而OPTIONS请求不会携带数据，导致这个请求被拦截了，直接返回了状态码，响应头中没携带解决跨域问题的头部信息，出现了跨域问题

          ----get请求不会附带options
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        //        session中未包含用户 则判定为未登录
        if (user == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        return true;
    }
}
