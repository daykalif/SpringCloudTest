package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

	@Autowired
	private UserMapper userMapper;

	@Test
	void testInsert() {
		User user = new User();
		user.setId(5L);
		user.setUsername("Lucy");
		user.setPassword("123");
		user.setPhone("18688990011");
		user.setBalance(200);
		//user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
		user.setInfo(UserInfo.of(24, "英文老师", "female"));
		user.setCreateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		//userMapper.saveUser(user);
		userMapper.insert(user);
	}

	@Test
	void testSelectById() {
		//User user = userMapper.queryUserById(5L);
		User user = userMapper.selectById(5L);
		System.out.println("user = " + user);
	}


	@Test
	void testQueryByIds() {
		//List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L));
		List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
		users.forEach(System.out::println);
	}

	@Test
	void testUpdateById() {
		User user = new User();
		user.setId(5L);
		user.setBalance(20000);
		//userMapper.updateUser(user);
		userMapper.updateById(user);
	}

	@Test
	void testDeleteUser() {
		//userMapper.deleteUser(5L);
		userMapper.deleteById(5L);
	}


	//---------------------演示 QueryWrapper-------------------------------------
	// 硬编码
	@Test
	void testQueryWrapper() {
		// 1.构建查询条件
		QueryWrapper<User> wrapper = new QueryWrapper<User>()
				.select("id", "username", "info", "balance")
				.like("username", "0")
				.ge("balance", 1000);
		//	2.查询
		List<User> users = userMapper.selectList(wrapper);
		users.forEach(System.out::println);
	}

	// 使用lambda表达式1
	@Test
	void testLambdaQueryWrapper1() {
		// 1.构建条件 WHERE username LIKE "%o%" AND balance >= 1000
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
				.select(User::getId, User::getUsername, User::getInfo, User::getBalance)
				.like(User::getUsername, "o")
				.ge(User::getBalance, 1000);
		// 2.查询
		List<User> users = userMapper.selectList(wrapper);
		users.forEach(System.out::println);
	}

	// 使用lambda表达式2
	@Test
	void testLambdaQueryWrapper2() {
		// 1.构建条件 WHERE username LIKE "%o%" AND balance >= 1000
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.lambda()
				.select(User::getId, User::getUsername, User::getInfo, User::getBalance)
				.like(User::getUsername, "o")
				.ge(User::getBalance, 1000);
		// 2.查询
		List<User> users = userMapper.selectList(wrapper);
		users.forEach(System.out::println);
	}

	//----------------------演示 UpdateWrapper------------------------------------
	@Test
	void testUpdateByQueryWrapper() {
		//  1.要变更的数据
		User user = new User();
		user.setBalance(2000);

		// 2.更新的条件
		QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username", "jack");

		// 3.执行更新
		userMapper.update(user, wrapper);
	}

	@Test
	void testUpdateWrapper() {
		List<Long> ids = List.of(1L, 2L, 4L);
		UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
				.setSql("balance = balance - 200")
				.in("id", ids);
		userMapper.update(null, wrapper);
	}


	//----------------------演示 自定义Wrapper------------------------------------
	@Test
	void testCustomWrapper() {
		// 1.准备自定义查询条件
		List<Long> ids = List.of(1L, 2L, 4L);
		QueryWrapper<User> wrapper = new QueryWrapper<User>().in("id", ids);

		// 2.调用mapper的自定义方法，直接传递Wrapper
		userMapper.updateBalanceByIds(wrapper, 200);
	}
}