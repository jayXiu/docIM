package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.entity.ChatRoom;
import com.junlin.repository.entity.ChatRoomRecord;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.entity.User;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.mapper.ChatRoomMapper;
import com.junlin.repository.service.*;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRecordService chatRoomRecordService;

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
                case "group": result = this.openGroup(imChannel.getUserId(), arr[2], imChannel); break;
            }

            return result;
        }

        return "command error";
    }

    private String openGroup(Long userId, String groupName, IMChannel imChannel){

        ChatRoom chatRoom = chatRoomService.getOne(Wrappers.<ChatRoom>lambdaQuery().eq(ChatRoom::getName, groupName));
        if(chatRoom == null){
            return "chatRoom not exists";
        }

        imChannel.setChatRoomId(chatRoom.getId()).setChatRoomName(chatRoom.getName()).setChatRoomType(ChatRoomType.GROUP);
        ChannelUtils.addChannel(imChannel.getHashCode(), imChannel);


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("open group " + groupName + " success\n");

        List<ChatRoomRecord> records = chatRoomRecordService.list(Wrappers.<ChatRoomRecord>lambdaQuery().eq(ChatRoomRecord::getChatRoomId, imChannel.getChatRoomId())
                .orderByDesc(ChatRoomRecord::getSendTime).last("limit 10"));
        if(records != null && records.size() > 0){
            for(int i = records.size() - 1; i >= 0; i--){
                ChatRoomRecord record = records.get(i);
                stringBuffer.append("FROM " + record.getSendUserName() + ":" + record.getContent() + ChannelUtils.date(record.getSendTime()) + "\n");
            }
        }

        return stringBuffer.toString();
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

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("open chat " + friendName + " success\n");

        List<ChatRoomRecord> records = chatRoomRecordService.list(Wrappers.<ChatRoomRecord>lambdaQuery().eq(ChatRoomRecord::getChatRoomId, imChannel.getChatRoomId())
                .orderByDesc(ChatRoomRecord::getSendTime).last("limit 10"));
        if(records != null && records.size() > 0){
            for(int i = records.size() - 1; i >= 0; i--){
                ChatRoomRecord record = records.get(i);
                stringBuffer.append("FROM " + record.getSendUserName() + ":" + record.getContent() + ChannelUtils.date(record.getSendTime()) + "\n");
            }
        }

        return stringBuffer.toString();
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
