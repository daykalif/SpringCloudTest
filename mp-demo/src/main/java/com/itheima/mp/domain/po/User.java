package com.itheima.mp.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.mp.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")  // 指定数据库的表名
public class User {

	/**
	 * 用户id
	 */
	@TableId(type = IdType.AUTO)    // 指定主键生成策略
	private Long id;

	/**
	 * 用户名
	 */
	@TableField("username") // 指定数据库字段名
	private String username;

	/**
	 * 密码
	 */
	//@TableField(exist = false)  // 不映射到数据库字段
	private String password;

	/**
	 * 注册手机号
	 */
	private String phone;

	/**
	 * 详细信息
	 */
	private String info;

	/**
	 * 使用状态（1正常 2冻结）
	 */
	private UserStatus status;

	/**
	 * 账户余额
	 */
	private Integer balance;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;
}
