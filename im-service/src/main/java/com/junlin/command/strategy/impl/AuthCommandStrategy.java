package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.entity.User;
import com.junlin.repository.service.UserService;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCommandStrategy implements CommandStrategy {

    private static String command = "auth";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length == 3){
            userService.lambdaQuery();

            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getName, arr[1]).eq(User::getPassword, arr[2]));
            if(user != null){
                imChannel.setUserId(user.getId());
                imChannel.setName(arr[1]);
                imChannel.setChannel(channel);
                imChannel.setHashCode(hashCode+"");
                ChannelUtils.addChannel(hashCode + "", imChannel);
                ChannelUtils.online(imChannel.getName(), imChannel.getUserId(), hashCode+"");



                return "auth success";
            }else{
                return "user not exists";
            }
        }

        return "command error";
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Boolean check(String message) {
        if(StringUtils.isEmpty(message)){
            return false;
        }

        return message.startsWith(command + " ");
    }
}
