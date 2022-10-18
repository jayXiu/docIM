package com.junlin.repository.mapper;

import com.junlin.repository.entity.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junlin.vo.FriendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 好友 Mapper 接口
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
public interface FriendMapper extends BaseMapper<Friend> {


    List<FriendVO> getFriends(@Param("userId") Long userId);

}
