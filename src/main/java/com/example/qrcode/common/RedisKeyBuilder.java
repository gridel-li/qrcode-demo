package com.example.qrcode.common;

import java.text.MessageFormat;

public class RedisKeyBuilder {

    /**
     * 登录二维码key
     */
    private static final String LOGIN_QRCODE_KEY = "qrcode:login:{0}";

    /**
     * loginName与二维码uuid关系
     */
    private static final String LOGIN_NAME_KEY = "qrcode:loginName:{0}";

    /**
     * 二维码uuid和loginName的关系
     */
    private static final String QRCODE_ACCESS_KEY = "qrcode:access:{0}";


    /**
     * 二维码与用户管理Key
     *
     * @param qrcodeId
     * @return
     */
    public static String getLoginQrcodeKey(String qrcodeId) {
        return MessageFormat.format(LOGIN_QRCODE_KEY, qrcodeId);
    }


    /**
     * 登录成功 令牌与登录用户key
     *
     * @param userId
     * @return
     */
    public static String getLoginNameKey(String userId) {
        return MessageFormat.format(LOGIN_NAME_KEY, userId);
    }


    /**
     * 登录成功 令牌与登录用户key
     *
     * @param qrcode
     * @return
     */
    public static String getQrcodeAccessKey(String qrcode) {
        return MessageFormat.format(QRCODE_ACCESS_KEY, qrcode);
    }

}
