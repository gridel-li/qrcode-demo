package com.example.qrcode.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qrcode.common.RedisRepository;
import com.example.qrcode.domain.AuthUser;
import com.example.qrcode.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * The type Token util.
 *
 * @author guoqing
 */
@Component
@ConfigurationProperties("jwt")
public class TokenUtil extends JwtTokenUtil {
	
	@Autowired
	private RedisRepository redisRepository;
	
	/**
	 * 获取用户信息
	 * @param token
	 * @return
	 */
	@Override
	public UserDetails getUserDetails(String token){
		String userName = getUsernameFromToken(token);
		String user = redisRepository.get("user_auth_info_"+userName);
		JSONObject jsonObj = JSON.parseObject(user);
		return jsonObj.toJavaObject(AuthUser.class);
	}

}