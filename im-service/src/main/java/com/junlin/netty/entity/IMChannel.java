package com.junlin.netty.entity;

import com.junlin.repository.enums.ChatRoomType;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IMChannel {

    private Long userId;

    private String name;

    private String hashCode;

    private Channel channel;

    private Long chatRoomId;

    private String chatRoomName;

    private ChatRoomType chatRoomType;
}
