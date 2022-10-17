package com.junlin.repository.mapper;

import com.junlin.repository.entity.ChatRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 聊天室 Mapper 接口
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
public interface ChatRoomMapper extends BaseMapper<ChatRoom> {


    ChatRoom getChatRoomByAB(@Param("aUserId") Long aUserId, @Param("bUserId") Long bUserId);

}
