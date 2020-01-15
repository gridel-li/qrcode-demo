package com.example.qrcode.service;

import com.example.qrcode.common.Message;

/**
 * @program: qrcode-demo
 * @Title: ILoginQrcodeService
 * @description:
 * @author: 李英杰
 * @create: 2020-01-15 11:34
 * @version: 1.0-SNAPSHOT
 */
public interface ILoginQrcodeService {


    Message<Boolean> qrcodeLogin(String qrcodeId, String userId);

    /**
     * 查询二维码的登录状态
     *
     * @param qrcodeId
     * @return
     */
    public Message getLoginQrcodeStatus(String qrcodeId);


}
