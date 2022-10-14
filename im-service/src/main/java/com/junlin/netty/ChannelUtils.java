package com.junlin.netty;

import com.junlin.netty.entity.IMChannel;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelUtils {


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

}
