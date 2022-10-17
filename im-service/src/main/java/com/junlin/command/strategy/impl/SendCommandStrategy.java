package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.netty.entity.UserInfo;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.service.FriendService;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendCommandStrategy implements CommandStrategy {

    private static String command = "send";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FriendService friendService;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length == 3){

            Friend friend = friendService.getOne(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, imChannel.getUserId())
                    .eq(Friend::getFriendName, arr[1]));
            if(friend == null){
                return "friend not exists\n";
            }

            Object target = redisUtil.get("USERINFO:"+arr[1]);
            if(target != null){
                UserInfo userInfo = (UserInfo) target;
                //todo 省略MQ
                IMChannel targetChannel = ChannelUtils.get(userInfo.getHashCode());
                if(targetChannel != null){
                    targetChannel.getChannel().writeAndFlush(ChannelUtils.date() + "FROM " + imChannel.getName() + "：" + arr[2] + "\n");
                    return "";
                }
            }

            //todo 不在线
            return arr[1] + " OffLine";
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
