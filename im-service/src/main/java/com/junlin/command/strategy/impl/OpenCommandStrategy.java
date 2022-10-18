package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.entity.ChatRoom;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.entity.User;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.mapper.ChatRoomMapper;
import com.junlin.repository.service.FriendService;
import com.junlin.repository.service.UserService;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//打开聊天室开启聊天
@Service
public class OpenCommandStrategy implements CommandStrategy {

    private static String command = "open";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Override
    public String executeCommand(String message, Channel channel) {

        //hashcode
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");

        String[] arr = message.split(" ");
        if(arr != null && arr.length == 3){

            String result = "";
            switch (arr[1]){
                case "chat": result = this.openChat(imChannel.getUserId(), arr[2], imChannel); break;
                case "group":
            }

            return result;
        }

        return "command error";
    }

    private String openChat(Long userId, String friendName, IMChannel imChannel){

        Friend friend = friendService.getOne(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, userId)
                .eq(Friend::getFriendName, friendName));
        if(friend == null){
            return "friend not exists";
        }

        ChatRoom chatRoom = chatRoomMapper.getChatRoomByAB(friend.getUserId(), friend.getFriendUserId());
        if(chatRoom == null){
            return "chatRoom not exists";
        }

        imChannel.setChatRoomId(chatRoom.getId()).setChatRoomName(chatRoom.getName()).setChatRoomType(ChatRoomType.SINGLE);
        ChannelUtils.addChannel(imChannel.getHashCode(), imChannel);

        return "open chat " +friendName+ " success";
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

        return  message.startsWith(command + " ");
    }
}
