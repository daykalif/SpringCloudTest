package com.itheima.mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserStatus {
	NORMAL(1, "正常"),
	FROZEN(2, "冻结"),
	;

	@EnumValue    // 添加了该注解的枚举值存储到数据库
	private final int value;
	@JsonValue    // 添加了该注解的枚举值返回到前端
	private final String desc;

	UserStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
}
