package com.example.qrcode.dao;

import com.example.qrcode.domain.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: qrcode-demo
 * @Title: SysUserRepository
 * @description:
 * @author: 李英杰
 * @create: 2020-01-09 17:49
 * @version: 1.0-SNAPSHOT
 */
public interface SysUserRepository extends JpaRepository<SysUser,Integer> {

    SysUser findByLoginName(String loginName);
}
