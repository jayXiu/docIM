package com.junlin.netty;

import com.junlin.netty.entity.IMChannel;
import com.junlin.netty.entity.Line;
import com.junlin.netty.entity.UserInfo;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelUtils {

    @Autowired
    private RedisUtil redisUtilBean;
    @Value("${im.listener}")
    private String listenerBean;

    private static RedisUtil redisUtil;
    private static String listener;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void postConstruct(){
        redisUtil = redisUtilBean;
        listener = listenerBean;
    }

    private static Map<String, IMChannel> channels = new HashMap<>();

    public static void addChannel(String hashCode, IMChannel imChannel){
        channels.put(hashCode, imChannel);
    }

    public static void removeChannel(String hashCode){
        channels.remove(hashCode);
    }

    public static IMChannel get(String hashCode){
        return channels.get(hashCode);
    }


    public static void online(String name, Long userId, String currHashCode){
        if(StringUtils.isNotEmpty(name)){

            //挤下线，断线操作
            Object target = redisUtil.get("USERINFO:"+name);
            if(target != null){
                UserInfo userInfo = (UserInfo) target;
                IMChannel imChannel = ChannelUtils.get(userInfo.getHashCode());
                if(imChannel != null && imChannel.getChannel() != null){
                    imChannel.getChannel().writeAndFlush("forced offline\n");
                    imChannel.getChannel().close();
                    ChannelUtils.removeChannel(imChannel.getHashCode());
                }
            }

            updateUserStatus(name, userId, currHashCode, Line.ONLINE);
        }
    }

    public static void offline(String name){
        if(StringUtils.isNotEmpty(name)){
            updateUserStatus(name, null, null, Line.OFFLINE);
        }
    }

    public static void updateUserStatus(String name,Long userId, String hashCode, Line line){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setName(name);
        userInfo.setHashCode(hashCode);
        userInfo.setLine(line);
        userInfo.setListener(listener);
        redisUtil.set("USERINFO:"+name, userInfo);
    }

    public static String date(){
        return "[" + sdf.format(new Date()) + "] ";
    }

    public static String date(Date date){
        return "[" + sdf.format(date) + "] ";
    }
}
