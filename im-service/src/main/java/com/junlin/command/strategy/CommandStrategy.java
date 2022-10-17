package com.junlin.command.strategy;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.channel.Channel;

public interface CommandStrategy {


    String executeCommand(String message, Channel channel);

    /**
     * 策略key
     * @return
     */
    String getCommand();


    Boolean check(String message);
}
