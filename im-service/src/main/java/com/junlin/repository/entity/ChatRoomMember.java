package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.junlin.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 聊天室成员
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_chat_room_member")
public class ChatRoomMember extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 聊天室ID
     */
    private Long chatRoomId;

    /**
     * 用户ID
     */
    private Long userId;

    private String name;

    /**
     * 读取的时间
     */
    private Date readTime;


}
