package com.example.qrcode.security;

import com.example.qrcode.common.Message;
import com.example.qrcode.exception.ExceptionResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: geometry-cloud-idaas
 * @Title: GeomtryAuthenticationEntryPoint
 * @description: 防止oauth重定向返回页面json信息
 * @author: 李英杰
 * @create: 2019-10-29 14:39
 * @version: 1.0-SNAPSHOT
 */
@Component
public class GeometryAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper gm;
    @Autowired
    public GeometryAuthenticationEntryPoint(ObjectMapper gm) {
        this.gm = gm;
    }
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        Message message = new Message<>();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        gm.writeValue(response.getOutputStream(),
                message.error(ExceptionResultCode.LOGIN_FAIL.getCode(),ExceptionResultCode.LOGIN_FAIL.getMessage()));    }
}