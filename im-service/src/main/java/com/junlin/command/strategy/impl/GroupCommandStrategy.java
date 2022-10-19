package com.junlin.command.strategy.impl;

import com.junlin.business.FriendBusiness;
import com.junlin.business.GroupBusiness;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.service.UserService;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

//群组操作
@Service
public class GroupCommandStrategy implements CommandStrategy {

    private static String command = "group";

    @Autowired
    private GroupBusiness groupBusiness;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length >= 2){

            List<String> list = Arrays.asList(arr);
            list.subList(2, list.size());


            String result = "";
            switch (arr[1]){
                case "show": result = groupBusiness.showGroup(imChannel.getUserId());break;
                case "add": result = groupBusiness.addGroup(imChannel, imChannel.getUserId(), arr[2], list);break;
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
