package com.junlin.netty.entity;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class IMChannel {

    private Long userId;

    private String name;

    private String hashCode;

    private Channel channel;
}
