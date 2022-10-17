package com.junlin.command.strategy.impl;

import com.junlin.business.FriendShipBusiness;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.enums.FriendshipStatus;
import com.junlin.repository.service.UserService;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//好友操作
@Service
public class FriendShipCommandStrategy implements CommandStrategy {

    private static String command = "friendship";

    @Autowired
    private UserService userService;
    @Autowired
    private FriendShipBusiness friendShipBusiness;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length >= 2){

            String result = "";
            switch (arr[1]){
                case "show": result = friendShipBusiness.showFriendShip(imChannel.getUserId());break;
                case "add": result = friendShipBusiness.addFriendShip(imChannel.getUserId(), arr[2]);break;
                case "agree": result = friendShipBusiness.agreeFriendShip(imChannel.getUserId(), arr[2], FriendshipStatus.AGREE);break;
                case "disagree": result = friendShipBusiness.agreeFriendShip(imChannel.getUserId(), arr[2], FriendshipStatus.DISAGREE);break;
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
