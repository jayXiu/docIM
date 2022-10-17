package com.junlin.command.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.netty.entity.UserInfo;
import com.junlin.repository.entity.ChatRoom;
import com.junlin.repository.entity.ChatRoomRecord;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.mapper.ChatRoomMapper;
import com.junlin.repository.service.ChatRoomRecordService;
import com.junlin.repository.service.ChatRoomService;
import com.junlin.repository.service.FriendService;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class SendCommandStrategy implements CommandStrategy {

    private static String command = "send";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRecordService chatRoomRecordService;
    @Autowired
    private ChatRoomMapper chatRoomMapper;

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
                return "friend not exists";
            }

            Object target = redisUtil.get("USERINFO:"+arr[1]);
            if(target != null){
                UserInfo userInfo = (UserInfo) target;

                //查找聊天室
                ChatRoom chatRoom = chatRoomMapper.getChatRoomByAB(imChannel.getUserId(), friend.getFriendUserId());

                if(chatRoom == null){
                    return "chat not exists";
                }

                //todo 省略MQ
                IMChannel targetChannel = ChannelUtils.get(userInfo.getHashCode());
                if(targetChannel != null){
                    targetChannel.getChannel().writeAndFlush(ChannelUtils.date() + "FROM " + imChannel.getName() + "：" + arr[2] + "\n");

                    //保存聊天记录
                    ChatRoomRecord record = new ChatRoomRecord();
                    record.setSendTime(new Date()).setSendUserId(imChannel.getUserId()).setContent(arr[2]).setChatRoomId(chatRoom.getId());
                    chatRoomRecordService.save(record);

                    return "success";
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
