package com.example.qrcode.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户Entity
 *
 * @author GuoqingLee
 */
@Data
@Entity
public class SysUser implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 超级管理用户ID
     */
    public static final Integer ADMIN_USER_ID = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 是否可用
     */
    private Boolean enabled;
    /**
     * 备注
     */
    private String remarks;

}