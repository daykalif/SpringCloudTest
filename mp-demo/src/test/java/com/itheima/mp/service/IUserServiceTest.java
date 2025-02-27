package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class IUserServiceTest {
	@Autowired
	private IUserService userService;

	@Test
	void testSaveUser() {
		User user = new User();

		user.setUsername("Lilei");
		user.setPassword("123456");
		user.setPhone("18688990011");
		user.setBalance(200);
		//user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"male\"}");
		user.setInfo(UserInfo.of(24, "英文老师", "female"));
		user.setCreateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());

		userService.save(user);
	}


	@Test
	void testQueryUser() {
		List<User> users = userService.listByIds(List.of(1L, 2L, 4L));
		users.forEach(System.out::println);
	}

	private User buildUser(int i) {
		User user = new User();

		user.setUsername("Lilei" + i);
		user.setPassword("123456");
		user.setPhone("" + (18688190000L + i));
		user.setBalance(200);
		//user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"male\"}");
		user.setInfo(UserInfo.of(24, "英文老师", "female"));
		user.setCreateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());

		return user;
	}

	@Test
	void testSaveOneByOne() {
		long b = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			userService.save(buildUser(i));
		}
		long e = System.currentTimeMillis();
		System.out.println("耗时：" + (e - b));
	}


	@Test
	void testSaveBatch() {
		//	我们每次批量插入1000条，插入100次即10万条数据

		//	1.准备一个容量为1000的集合
		List<User> list = new ArrayList<>(1000);
		long b = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			// 2.添加一个user
			list.add(buildUser(i));

			// 3.每1000条批量插入一次
			if (list.size() == 1000) {
				userService.saveBatch(list);
				// 4.清空集合，准备下一批数据
				list.clear();
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("耗时：" + (e - b));
	}


	@Test
	void  testPageQuery() {
		int pageNo = 1, pageSize = 2;
		// 1.准备分页条件
		Page<User> page = Page.of(pageNo, pageSize);
		// 1.1 分页条件
		page.addOrder(new OrderItem("balance", true));    // 首先按照balance排序，升序
		page.addOrder(new OrderItem("id", false));    // 其次是按照id排

		// 2.分页查询
		Page<User> p = userService.page(page);

		// 3.解析
		long total = p.getTotal();
		List<User> records = p.getRecords();
		records.forEach(System.out::println);
		System.out.println("总页数：" + p.getPages());
		System.out.println("总记录数：" + total);
	}
}