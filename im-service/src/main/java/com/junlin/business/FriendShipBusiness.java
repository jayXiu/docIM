package com.junlin.business;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.junlin.repository.entity.Friend;
import com.junlin.repository.entity.Friendship;
import com.junlin.repository.entity.FriendshipLog;
import com.junlin.repository.entity.User;
import com.junlin.repository.enums.FriendshipStatus;
import com.junlin.repository.service.FriendService;
import com.junlin.repository.service.FriendshipLogService;
import com.junlin.repository.service.FriendshipService;
import com.junlin.repository.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class FriendShipBusiness {

    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipLogService friendshipLogService;

    @Transactional
    public String agreeFriendShip(Long userId, String friendName, FriendshipStatus status){

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
