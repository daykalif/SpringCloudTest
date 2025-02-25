package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

	//void saveUser(User user);
	//
	//void deleteUser(Long id);
	//
	//void updateUser(User user);
	//
	//User queryUserById(@Param("id") Long id);
	//
	//List<User> queryUserByIds(@Param("ids") List<Long> ids);


	//@Select("UPDATE user SET balance = balance - #{money} ${ew.customSqlSegment}")
	void updateBalanceByIds(@Param(Constants.WRAPPER) QueryWrapper<User> wrapper, @Param("money") int money);    // @Param(Constants.WRAPPER)：用于where 条件


	@Update("UPDATE user SET balance = balance - #{money} WHERE id = #{id}")
	void deductBalance(@Param("id") Long id, @Param("money") Integer money);
}
