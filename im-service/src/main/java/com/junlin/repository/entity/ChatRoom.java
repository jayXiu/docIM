package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junlin.common.mybatis.base.BaseEntity;
import com.junlin.repository.enums.ChatRoomType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 聊天室
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_chat_room")
public class ChatRoom extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 聊天室名称
     */
    private String name;

    /**
     * SINGLE单人 GROUP群组
     */
    private ChatRoomType type;


}
