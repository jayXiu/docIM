package com.junlin.repository.service.impl;

import com.junlin.repository.entity.Friend;
import com.junlin.repository.mapper.FriendMapper;
import com.junlin.repository.service.FriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友 服务实现类
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

}
