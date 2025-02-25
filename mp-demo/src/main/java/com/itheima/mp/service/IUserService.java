package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;

public interface IUserService extends IService<User> {
	void deductBalance(Long id, Integer money);    // 快捷键：command + enter，创建单元测试
}
