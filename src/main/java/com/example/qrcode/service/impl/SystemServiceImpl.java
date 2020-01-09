package com.example.qrcode.service.impl;

import com.example.qrcode.dao.SysUserRepository;
import com.example.qrcode.domain.SysUser;
import com.example.qrcode.service.ISystemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 *
 * @author Guoqing
 */
@Service
public class SystemServiceImpl implements ISystemService {

    @Resource
    private SysUserRepository sysUserRepository;


    @Override
    public SysUser getUserByLoginName(String loginName) {
        return sysUserRepository.findByLoginName(loginName);
    }
}
