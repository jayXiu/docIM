package com.junlin.repository.service.impl;

import com.junlin.repository.entity.Friendship;
import com.junlin.repository.mapper.FriendshipMapper;
import com.junlin.repository.service.FriendshipService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友关系 服务实现类
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

}
