package com.example.qrcode.common;

/**
 * describe：项目常量统一管理
 *
 * @author: panchaoyue
 * @create-date: 2019-08-21
 */
public class CommonConstants {


    public static class ResultObj {
        public static final Integer SUCCESS_CODE = 200;
        public static final Integer ERROR_CODE = 500;
        public static final Integer ERROR_TOKEN = 401;

        public static final String UNKNOWN_ERROR_MSG = "未知错误";
        public static final String HYSTRIX_ERROR_MSG = "服务器丢失";
        public static final String PARAMETER_ERROR_MSG = "参数错误";
        public static final String PERMISSION_ERROR_MSG = "无权限进行此操作";

        public static final String SUCCESS_MSG = "请求成功";
        public static final String ERROR_MSG = "操作失败";
        public static final String UPLOAD_FILE = "./";
        public static final String LOGIN_FAIL_MSG = "用户未登陆";
        public static final String INFORMATION_FAIL_MSG = "信息缺失";
        public static final String LOGIN_ERROR_MSG = "登陆失败";
        public static final String LOGIN_ERROR_NOT_REGISTERED_MSG = "用户未注册";
    }
}
