package org.fjsei.yewu.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
        //header只支持ASCII  URLEncoder.encode(“我是汉字”,"UTF-8")
        //若401返回码，到这！   若设置permitAnyURL: false

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "您还没证明您有权限访问该区域资源!");
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Forget it! You have no Quan Xiang for this resource,Yet!");
    }
}

