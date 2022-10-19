package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.junlin.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 聊天室聊天记录
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_chat_room_record")
public class ChatRoomRecord extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 聊天室ID
     */
    private Long chatRoomId;

    /**
     * 发送者用户ID
     */
    private Long sendUserId;

    private String sendUserName;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 聊天内容
     */
    private String content;


}
