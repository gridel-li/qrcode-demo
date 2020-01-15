package com.example.qrcode.service.impl;

import com.example.qrcode.common.Message;
import com.example.qrcode.common.RedisKeyBuilder;
import com.example.qrcode.common.RedisRepository;
import com.example.qrcode.security.JwtTokenUtil;
import com.example.qrcode.service.ILoginQrcodeService;
import com.example.qrcode.service.ISystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangck
 * @date 2019/7/10
 */
@Service("LoginQrcodeService")
public class LoginQrcodeServiceImpl implements ILoginQrcodeService {

    private final static Logger logger = LoggerFactory.getLogger(LoginQrcodeServiceImpl.class);


    @Autowired
    private RedisRepository redisRepository;
    @Resource
    private ISystemService systemService;

    @Override
    public Message qrcodeLogin(String qrcodeId, String userId) {
        String loginQrcodeKey = RedisKeyBuilder.getLoginQrcodeKey(qrcodeId);
        if (redisRepository.exists(loginQrcodeKey)) {
           redisRepository.setExpire(loginQrcodeKey, userId, 180);
        } else {
            logger.info("二维码已不存在，qrcodeId={}", qrcodeId);
            return Message.error("二维码登录失败");
        }
        return null;
    }

    @Override
    public Message getLoginQrcodeStatus(String qrcodeId) {
        return Message.ok(JwtTokenUtil.TOKEN_TYPE_BEARER + " " );

    }

}