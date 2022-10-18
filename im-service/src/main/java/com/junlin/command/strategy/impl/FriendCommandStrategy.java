package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.business.FriendBusiness;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.entity.User;
import com.junlin.repository.enums.FriendshipStatus;
import com.junlin.repository.service.UserService;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//好友操作
@Service
public class FriendCommandStrategy implements CommandStrategy {

    private static String command = "friend";

    @Autowired
    private UserService userService;
    @Autowired
    private FriendBusiness friendBusiness;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length >= 2){

            String result = "";
            switch (arr[1]){
                case "show": result = friendBusiness.showFriend(imChannel.getUserId());break;
                case "add": result = friendBusiness.addFriend(imChannel.getUserId(), arr[2]);break;
            }

            return result;
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
