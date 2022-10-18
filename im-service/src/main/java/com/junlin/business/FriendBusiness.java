package com.junlin.business;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.repository.entity.ChatRoom;
import com.junlin.repository.entity.ChatRoomMember;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.entity.User;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.mapper.ChatRoomMapper;
import com.junlin.repository.mapper.FriendMapper;
import com.junlin.repository.service.ChatRoomMemberService;
import com.junlin.repository.service.ChatRoomService;
import com.junlin.repository.service.FriendService;
import com.junlin.repository.service.UserService;
import com.junlin.vo.FriendVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendBusiness {

    @Autowired
    private UserService userService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomMemberService chatRoomMemberService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private FriendMapper friendMapper;


    @Transactional
    public String addFriend(Long userId, String friendName){

        //用户不存在
        User aUser = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        if(aUser == null){
            return "user not exists";
        }

        //用户不存在
        User bUser = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getName, friendName));
        if(bUser == null){
            return "user not exists";
        }

        Friend friend1 = friendService.getOne(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, aUser.getId())
                .eq(Friend::getFriendUserId, bUser.getId()));
        if(friend1 == null){
            friend1 = new Friend();
            friend1.setUserId(aUser.getId()).setName(aUser.getName()).setFriendUserId(bUser.getId()).setFriendName(bUser.getName());
            friendService.save(friend1);
        }

        Friend friend2 = friendService.getOne(Wrappers.<Friend>lambdaQuery().eq(Friend::getUserId, bUser.getId())
                .eq(Friend::getFriendUserId, aUser.getId()));
        if(friend2 == null){
            friend2 = new Friend();
            friend2.setUserId(bUser.getId()).setName(bUser.getName()).setFriendUserId(aUser.getId()).setFriendName(aUser.getName());
            friendService.save(friend2);
        }

        //查找聊天室
        ChatRoom chatRoom = chatRoomMapper.getChatRoomByAB(aUser.getId(), bUser.getId());
        if(chatRoom == null){
            chatRoom = new ChatRoom();
            chatRoom.setName(aUser.getName()+"|"+bUser.getName()).setType(ChatRoomType.SINGLE);
            chatRoomService.save(chatRoom);

            ChatRoomMember chatRoomMember1 = new ChatRoomMember();
            chatRoomMember1.setChatRoomId(chatRoom.getId()).setUserId(aUser.getId()).setName(aUser.getName()).setReadTime(new Date());

            ChatRoomMember chatRoomMember2 = new ChatRoomMember();
            chatRoomMember2.setChatRoomId(chatRoom.getId()).setUserId(bUser.getId()).setName(bUser.getName()).setReadTime(new Date());
            chatRoomMemberService.saveBatch(Arrays.asList(chatRoomMember1, chatRoomMember2));
        }

        return "success";
    }


    public String showFriend(Long userId){

        StringBuffer stringBuffer = new StringBuffer();

        List<FriendVO> friends = friendMapper.getFriends(userId);

        if(friends != null && friends.size() > 0){
            stringBuffer.append("\n");
            Map<Long, List<FriendVO>> map = friends.stream().collect(Collectors.groupingBy(FriendVO::getChatRoomId));
            for(Map.Entry<Long, List<FriendVO>> entry : map.entrySet()){
                List<FriendVO> list = entry.getValue();
                if(list != null && list.size() > 0){
                    String friendName = "";
                    Integer unread = 0;

                    for(FriendVO friend : list){
                        if(friend.getUserId().longValue() != userId){
                            friendName = friend.getName();
                        }else{
                            unread = friend.getUnread();
                        }
                    }

                    stringBuffer.append(friendName + "(" + unread + ")\n");
                }
            }
        }

        if(StringUtils.isEmpty(stringBuffer.toString())){
            return "no friend";
        }

        return stringBuffer.toString();
    }

}
