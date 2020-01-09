package com.example.qrcode.security;

import com.example.qrcode.common.Message;
import com.example.qrcode.exception.ExceptionResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: geometry-cloud-idaas
 * @Title: GeometryAccessDeniedHandler
 * @description: 认证过的用户访问无权限资源时的异常 防止oauth重定向返回页面json信息
 * @author: 李英杰
 * @create: 2019-10-29 14:40
 * @version: 1.0-SNAPSHOT
 */
@Component
public class GeometryAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper gm;
    @Autowired
    public GeometryAccessDeniedHandler(ObjectMapper gm) {
        this.gm = gm;
    }
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        Message message = new Message<>();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        gm.writeValue(response.getOutputStream(),message.error(ExceptionResultCode.PERMISSION_NONE.getCode(),ExceptionResultCode.PERMISSION_NONE.getMessage()));
    }
}