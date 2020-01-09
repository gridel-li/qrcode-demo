package com.example.qrcode.exception;

/**
 * describe：异常返回码统一管理
 *
 * @author: panchaoyue
 * @create-date: 2019-08-21
 */
public enum ExceptionResultCode {
    //系统异常
    PARAM_ERR(500, "入参非法"),
    DUPLICATE_KEY_ERR(501, "已有记录"),
    UPLOAD_FILE(502, "上传文件异常"),
    DOWNLOAD_FILE(503, "下载文件不存在"),
    SYSTEM_ERR(-1, "服务器异常"),
    AUTHENTICATION_FORBIDDEN(-2, "授权失败，禁止访问"),
    //模板异常
    INVALIDATE_TEMPLATE_ID(1001, "模板不可用"),
    TEMPLATE_MATCH_ERROR(1002, "模板信息错误"),
    //应用操作异常
    APP_NAME_REPEAT(2001, "应用名称重复"),
    APP_NOT_EXIST(2002, "应用不存在"),
    //登录异常
    NOT_LOGGED(3000, "当前用户未登录"),
    LOGIN_FAIL(3001,"登录失败"),
    LOGIN_EXPIRED(3002,"登录过期"),
    LOGOUT(3003,"退出登录"),
    LOGIN_PASSWORD_FAIL(3004,"密码错误"),
    PERMISSION_NONE(3005,"权限不足"),
    //同步配置信息异常
    SYNC_CONF_APP(4001, "该应用已经配置，请修改"),

    //OAuth2异常返回信息
    AUTH_ERROR(5001, "用户名或密码错误！"),
    TOKEN_INVALID(5002, "Token失效，请重新获取！"),
    ERROR_GRANT_TYPE(5003, "授权类型不匹配，请在应用信息查看允许的授权类型！"),
    AUTH_CODE_ERROR(5004, "授权码无效！"),
    UNKNOWN_ERROR(5005, "系统内部异常，请联系管理员"),
    ERROR_CLIENT(5006, "未找到匹配应用，请检查client_id或者client_secret是否正确"),
    REFRESH_TOKEN_ERROR(5007, "无效的refresh_token，刷新Token失败"),
    APP_STATUS_DISABLE(5008, "应用已被禁用，如需使用请联系管理员"),
    REDIRECT_URI_ERROR(5009, "无效的重定向地址，与注册值不匹配"),

    //用户操作异常 同步bi项目异常码
    NAME_STATUS(7001,"名称格式有误"),
    PASSWORD_STATUS(7002,"密码格式有误"),
    PHONE_NO_STATUS(7003,"手机号格式有误"),
    ID_CARD_STATUS(7004,"证件格式有误"),
    CURRENT_USER(7005, "当前用户不存在"),


    SYNC_REMOTE_ERROR(8001, "远程调用接口失败，请检查接口地址是否正确！"),

;
    /**
     * 返回码
     */
    private int code;

    /**
     * 返回结果描述
     */
    private String message;

    ExceptionResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    //Enum编译时就转成常量了 无法创建对象 此处充当创建对象的作用 创建之后对code和msg进行修改
    public static ExceptionResultCode getInstance() {
        return ExceptionResultCode.SYSTEM_ERR;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
