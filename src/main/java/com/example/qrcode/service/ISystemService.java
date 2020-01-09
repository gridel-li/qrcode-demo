package com.example.qrcode.service;

import com.example.qrcode.domain.SysUser;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 *
 * @author Guoqing
 */
public interface ISystemService{

    SysUser getUserByLoginName(String loginName);
}
