package com.junlin.business;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.netty.entity.IMChannel;
import com.junlin.repository.entity.ChatRoom;
import com.junlin.repository.entity.ChatRoomMember;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.mapper.ChatRoomMapper;
import com.junlin.repository.service.ChatRoomMemberService;
import com.junlin.repository.service.ChatRoomService;
import com.junlin.repository.service.FriendService;
import com.junlin.vo.GroupVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

//群组操作
@Service
public class GroupBusiness {

    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomMemberService chatRoomMemberService;
    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Transactional
    public String addGroup(IMChannel imChannel, Long userId, String groupName, List<String> members){

        ChatRoom chatRoom = chatRoomService.getOne(Wrappers.<ChatRoom>lambdaQuery().eq(ChatRoom::getName, groupName).eq(ChatRoom::getType, ChatRoomType.GROUP)
                .exists("select 1 from t_chat_room_member a where a.chat_room_id = id and a.user_id = " + userId));

        if(chatRoom == null){
            //新增群聊
            chatRoom = new ChatRoom();
            chatRoom.setName(groupName).setType(ChatRoomType.GROUP);
            chatRoomService.save(chatRoom);

            Date date = new Date();
            List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

            //添加朋友
            if(members != null && members.size() > 0){
                List<Friend> friends = friendService.list(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, userId).in(Friend::getFriendName, members));
                if(friends != null && friends.size() > 0){
                    for(Friend friend : friends){
                        ChatRoomMember chatRoomMember = new ChatRoomMember();
                        chatRoomMember.setChatRoomId(chatRoom.getId()).setReadTime(date).setUserId(friend.getFriendUserId()).setName(friend.getFriendName());
                        chatRoomMembers.add(chatRoomMember);
                    }
                }
            }

            //添加自己
            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setChatRoomId(chatRoom.getId()).setReadTime(date).setUserId(userId).setName(imChannel.getName());
            chatRoomMembers.add(chatRoomMember);
            chatRoomMemberService.saveBatch(chatRoomMembers);

            return "group create success";
        }else{

            //新增群聊人员
            if(members != null && members.size() > 0){
                List<Friend> friends = friendService.list(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, userId).in(Friend::getFriendName, members));
                List<Long> ids1 = friends.stream().map(Friend::getFriendUserId).collect(Collectors.toList());
                Map<Long, Friend> friendMap = friends.stream().collect(Collectors.toMap(Friend::getFriendUserId, p -> p));

                List<ChatRoomMember> chatRoomMembers = chatRoomMemberService.list(Wrappers.<ChatRoomMember>lambdaQuery().eq(ChatRoomMember::getChatRoomId, chatRoom.getId()));
                List<Long> ids2 = chatRoomMembers.stream().filter(p -> {
                    return !p.getName().equals(imChannel.getName());
                }).map(ChatRoomMember::getUserId).collect(Collectors.toList());

                //去重
                ids1.removeAll(ids2);
                if(ids1.size() > 0){
                    Date date = new Date();
                    List<ChatRoomMember> result = new ArrayList<>();

                    for(Long id : ids1){
                        Friend friend = friendMap.get(id);
                        if(friend != null){
                            ChatRoomMember chatRoomMember = new ChatRoomMember();
                            chatRoomMember.setChatRoomId(chatRoom.getId()).setReadTime(date).setUserId(friend.getFriendUserId()).setName(friend.getFriendName());
                            chatRoomMembers.add(chatRoomMember);
                        }
                    }
                    if(result.size() > 0){
                        chatRoomMemberService.saveBatch(result);
                    }
                }
            }

            return "group add success";
        }
    }


    public String showGroup(Long userId){

        StringBuffer stringBuffer = new StringBuffer();

        List<GroupVO> groups = chatRoomMapper.getGroupsByUserId(userId);
        if(groups != null && groups.size() > 0){
            stringBuffer.append("\n");
            for(GroupVO groupVO : groups){
                stringBuffer.append(groupVO.getName() + "(" + groupVO.getUnread() + ")\n");
            }
        }

        if(StringUtils.isEmpty(stringBuffer.toString())){
            return "no friend";
        }

        return stringBuffer.toString();
    }
}
