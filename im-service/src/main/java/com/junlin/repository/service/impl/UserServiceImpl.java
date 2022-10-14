package com.junlin.repository.service.impl;

import com.junlin.repository.entity.User;
import com.junlin.repository.mapper.UserMapper;
import com.junlin.repository.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
