package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;

import java.util.List;

public interface IUserService extends IService<User> {
	void deductBalance(Long id, Integer money);    // 快捷键：command + enter，创建单元测试

	List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance);
}
