package com.junlin.business;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.repository.entity.*;
import com.junlin.repository.enums.ChatRoomType;
import com.junlin.repository.enums.FriendshipStatus;
import com.junlin.repository.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FriendShipBusiness {

    @Autowired
    private UserService userService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomMemberService chatRoomMemberService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipLogService friendshipLogService;

    @Transactional
    public String agreeFriendShip(Long userId, String friendName, FriendshipStatus status){

        //用户不存在
        User aUser = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        if(aUser == null){
            return "user not exists";
        }

        //用户不存在
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getName, friendName));
        if(user == null){
            return "user not exists";
        }

        Friendship friendship = friendshipService.getOne(Wrappers.<Friendship>lambdaQuery().eq(Friendship::getAUserId, user.getId())
                .eq(Friendship::getBUserId, userId));
        if(friendship != null && friendship.getStatus() == FriendshipStatus.ADD){
            friendship.setStatus(status);
            friendshipService.updateById(friendship);

            FriendshipLog friendshipLog = new FriendshipLog();
            friendshipLog.setFriendshipId(friendship.getId()).setUserId(userId)
                    .setLogTime(new Date()).setStatus(status);
            friendshipLogService.save(friendshipLog);

            if(status == FriendshipStatus.AGREE){
                //新增朋友
                Friend friend1 = new Friend();
                friend1.setUserId(aUser.getId()).setName(aUser.getName()).setFriendUserId(user.getId()).setFriendName(user.getName());

                Friend friend2 = new Friend();
                friend2.setUserId(user.getId()).setName(user.getName()).setFriendUserId(aUser.getId()).setFriendName(aUser.getName());
                friendService.saveBatch(Arrays.asList(friend1, friend2));

                //新增聊天室
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setName(friendship.getName()+"|"+friendship.getFriendName()).setType(ChatRoomType.SINGLE);
                chatRoomService.save(chatRoom);

                ChatRoomMember chatRoomMember1 = new ChatRoomMember();
                chatRoomMember1.setChatRoomId(chatRoom.getId()).setUserId(aUser.getId()).setName(aUser.getName()).setReadTime(new Date());

                ChatRoomMember chatRoomMember2 = new ChatRoomMember();
                chatRoomMember2.setChatRoomId(chatRoom.getId()).setUserId(user.getId()).setName(user.getName()).setReadTime(new Date());
                chatRoomMemberService.saveBatch(Arrays.asList(chatRoomMember1, chatRoomMember2));
            }

            return "success";
        }

        return "friendShip not exists";
    }

    @Transactional
    public String addFriendShip(Long userId, String friendName){

        //用户不存在
        User aUser = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        if(aUser == null){
            return "user not exists";
        }

        //用户不存在
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getName, friendName));
        if(user == null){
            return "user not exists";
        }

        //关系已存在
        Friendship friendship = friendshipService.getOne(Wrappers.<Friendship>lambdaQuery().eq(Friendship::getAUserId, userId)
                .eq(Friendship::getBUserId, user.getId()));
        if(friendship != null &&
                (friendship.getStatus() == FriendshipStatus.AGREE || friendship.getStatus() == FriendshipStatus.ADD)){
            return "friendship has exists";
        }

        friendship = friendshipService.getOne(Wrappers.<Friendship>lambdaQuery().eq(Friendship::getAUserId, user.getId())
                .eq(Friendship::getBUserId, userId));
        if(friendship != null &&
                (friendship.getStatus() == FriendshipStatus.AGREE || friendship.getStatus() == FriendshipStatus.ADD)){
            return "friendship has exists";
        }

        Friendship result = new Friendship();
        if(friendship != null){
            result = friendship;
        }
        result.setAUserId(userId).setName(aUser.getName()).setBUserId(user.getId()).setFriendName(user.getName()).setStatus(FriendshipStatus.ADD);
        friendshipService.saveOrUpdate(result);

        FriendshipLog friendshipLog = new FriendshipLog();
        friendshipLog.setFriendshipId(result.getId()).setUserId(userId)
                .setLogTime(new Date()).setStatus(FriendshipStatus.ADD);
        friendshipLogService.save(friendshipLog);
        return "success";
    }

    public String showFriendShip(Long userId){

        StringBuffer stringBuffer = new StringBuffer();

        List<Friendship> friendshipList1 = friendshipService.list(Wrappers.<Friendship>lambdaQuery()
                .eq(Friendship::getAUserId, userId)
                .or().eq(Friendship::getBUserId, userId)
                .orderByDesc(Friendship::getCreateTime));
        if(friendshipList1 != null && friendshipList1.size() > 0){
            stringBuffer.append("\n");
            for(Friendship friendship : friendshipList1){
                stringBuffer.append(friendship.getName()+"\t" + friendship.getFriendName() + "\t" + friendship.getStatus() + "\n");
            }
        }

        String result = stringBuffer.toString();
        if(StringUtils.isEmpty(result)){
            result = "no friendship";
        }
        return result;
    }

}
