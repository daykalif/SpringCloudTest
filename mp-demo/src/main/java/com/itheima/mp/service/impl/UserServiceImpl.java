package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
	@Override
	@Transactional    // 开启事务
	public void deductBalance(Long id, Integer money) {
		// 1.查询用户
		User user = getById(id);    // 在service中调自己，直接调就行了，不需要注入

		// 2.校验用户状态
		if (user == null || user.getStatus() == UserStatus.FROZEN) {
			throw new RuntimeException("用户状态异常！");
		}

		// 3.扣减余额
		if (user.getBalance() < money) {
			throw new RuntimeException("余额不足！");
		}

		// 4.更新用户余额 update user set balance = balance - ? where id = ?
		//baseMapper.deductBalance(id, money);

		int remainBalance = user.getBalance() - money;
		lambdaUpdate()
				.set(User::getBalance, remainBalance)
				.set(remainBalance == 0, User::getStatus, UserStatus.FROZEN)
				.eq(User::getId, id)
				.eq(User::getBalance, user.getBalance())    // 乐观锁
				.update();
	}


	/**
	 * 根据复杂条件查询用户
	 *
	 * @param name
	 * @param status
	 * @param minBalance
	 * @param maxBalance
	 * @return
	 */
	@Override
	public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
		return lambdaQuery().like(name != null, User::getUsername, name)
				.eq(status != null, User::getStatus, status)
				.ge(minBalance != null, User::getBalance, minBalance)
				.le(maxBalance != null, User::getBalance, maxBalance)
				.list();
	}

	@Override
	public UserVO queryUserAndAddressById(Long id) {
		// 1.查询用户
		User user = getById(id);
		if (user == null || user.getStatus() == UserStatus.FROZEN) {
			throw new RuntimeException("用户状态异常！");
		}

		//	2.查询地址
		List<Address> addresses = Db.lambdaQuery(Address.class)
				.eq(Address::getUserId, id)
				.list();

		// 3.封装VO
		// 3.1 转User的PO为VO
		UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
		// 3.2 转Address的PO为VO
		if (CollUtil.isNotEmpty(addresses)) {
			userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
		}

		return userVO;
	}

	@Override
	public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
		// 1.查询用户
		List<User> users = listByIds(ids);
		if (CollUtil.isEmpty(users)) {
			return Collections.emptyList();
		}

		// 2.查询地址
		// 2.1 获取用户id集合
		List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
		// 2.2 根据用户id查询地址
		List<Address> addresses = Db.lambdaQuery(Address.class)
				.in(Address::getUserId, userIds)
				.list();
		// 2.3 转换地址VO
		List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
		// 2.4 用户地址集合分组处理，相同用户的放入一个集合（组）中
		Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
		if (CollUtil.isNotEmpty(addressVOList)) {
			addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
		}


		// 3.转VO返回
		List<UserVO> list = new ArrayList<>(users.size());
		for (User user : users) {
			// 3.1 转User的PO为VO
			UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
			list.add(vo);
			// 3.2 转Address的PO为VO
			if (CollUtil.isNotEmpty(addresses)) {
				vo.setAddresses(addressMap.get(user.getId()));
			}
		}

		return list;
	}

	@Override
	public PageDTO<UserVO> queryUsersPage(UserQuery query) {
		String name = query.getName();
		Integer status = query.getStatus();

		// 1.构建分页条件
		Page<User> page = Page.of(query.getPageNo(), query.getPageSize());

		// 2.构建排序条件
		if (StrUtil.isNotBlank(query.getSortBy())) {
			// 不为空
			page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
		} else {
			// 为空，默认按照更新时间排序
			page.addOrder(new OrderItem("update_time", false));
		}


		// 2.分页查询
		Page<User> p = lambdaQuery()
				.like(name != null, User::getUsername, name)
				.eq(status != null, User::getStatus, status)
				.page(page);

		// 3.封装VO结果
		PageDTO<UserVO> dto = new PageDTO<>();
		// 3.1 总条数
		dto.setTotal(p.getTotal());
		// 3.2 总页数
		dto.setPages(p.getPages());
		// 3.3 当前页数据
		List<User> records = p.getRecords();
		if (CollUtil.isEmpty(records)) {
			dto.setList(Collections.emptyList());
			return dto;
		}
		// 3.4 转换为VO
		dto.setList(BeanUtil.copyToList(records, UserVO.class));

		// 4.返回
		return dto;
	}
}
