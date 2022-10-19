package com.junlin.business;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.netty.ChannelUtils;
import com.junlin.netty.entity.IMChannel;
import com.junlin.netty.entity.UserInfo;
import com.junlin.repository.entity.ChatRoomMember;
import com.junlin.repository.entity.ChatRoomRecord;
import com.junlin.repository.service.ChatRoomMemberService;
import com.junlin.repository.service.ChatRoomRecordService;
import com.junlin.repository.service.ChatRoomService;
import com.junlin.repository.service.FriendService;
import com.junlin.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatRoomBusiness {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomMemberService chatRoomMemberService;
    @Autowired
    private ChatRoomRecordService chatRoomRecordService;


    public void chatFree(IMChannel imChannel, String message){

        if(StringUtils.isEmpty(message)){
            return;
        }

        Date date = new Date();
        //保存聊天记录
        ChatRoomRecord record = new ChatRoomRecord();
        record.setSendTime(date).setSendUserId(imChannel.getUserId()).setSendUserName(imChannel.getName()).setContent(message).setChatRoomId(imChannel.getChatRoomId());
        chatRoomRecordService.save(record);

        List<ChatRoomMember> members = chatRoomMemberService.list(Wrappers.<ChatRoomMember>lambdaQuery()
                .eq(ChatRoomMember::getChatRoomId, imChannel.getChatRoomId())
                .ne(ChatRoomMember::getUserId, imChannel.getUserId()));

        if(members != null && members.size() > 0){
            for(ChatRoomMember member : members){
                Object target = redisUtil.get("USERINFO:"+  member.getName());
                if(target != null) {
                    UserInfo userInfo = (UserInfo) target;
                    //todo 省略MQ
                    IMChannel targetChannel = ChannelUtils.get(userInfo.getHashCode());
                    if(targetChannel != null && targetChannel.getChatRoomId() != null &&
                            targetChannel.getChatRoomId().longValue() == imChannel.getChatRoomId() && targetChannel.getChannel().isActive()){
                        targetChannel.getChannel().writeAndFlush("FROM " + imChannel.getName() + "：" + message + " " +ChannelUtils.date(date) +"\n");
                    }
                }
            }
        }
    }

    public void closeChat(IMChannel imChannel){
        ChatRoomMember chatRoomMember = chatRoomMemberService.getOne(Wrappers.<ChatRoomMember>lambdaQuery()
                .eq(ChatRoomMember::getChatRoomId, imChannel.getChatRoomId())
                .eq(ChatRoomMember::getUserId, imChannel.getUserId()));

        if(chatRoomMember != null){
            chatRoomMember.setReadTime(new Date());
            chatRoomMemberService.updateById(chatRoomMember);
        }

        imChannel.setChatRoomId(null).setChatRoomName(null).setChatRoomType(null);
        ChannelUtils.addChannel(imChannel.getHashCode(), imChannel);
    }

}
