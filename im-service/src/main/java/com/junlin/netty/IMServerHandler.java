package com.junlin.netty;

import com.junlin.business.ChatRoomBusiness;
import com.junlin.command.CommandInitiator;
import com.junlin.command.strategy.CommandStrategy;
import com.junlin.command.strategy.impl.AuthCommandStrategy;
import com.junlin.netty.entity.IMChannel;
import com.junlin.netty.entity.Line;
import com.junlin.netty.entity.UserInfo;
import com.junlin.utils.RedisUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
@Scope("prototype")
public class IMServerHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private RedisUtil redisUtil;
    @Value("${im.listener}")
    private String listener;
    @Autowired
    private CommandInitiator commandInitiator;
    @Autowired
    private AuthCommandStrategy authCommandStrategy;
    @Autowired
    private ChatRoomBusiness chatRoomBusiness;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {

        String msg = s.replace("\n", "");
        log.info(msg);

        String date = ChannelUtils.date();

        //鉴权
        Channel channel = ctx.channel();
        int hashCode = channel.hashCode();
        IMChannel imChannel = ChannelUtils.get(hashCode + "");
        if((imChannel == null || imChannel.getUserId() == null) && !authCommandStrategy.check(msg)){
            channel.writeAndFlush(date + "please auth" + "\n");
            return;
        }

        //自由聊天
        if(imChannel.getChatRoomId() != null){
            if("close chat".equals(msg)){
                chatRoomBusiness.closeChat(imChannel);
                channel.writeAndFlush(date + "close success" + "\n");
                return;
            }

            chatRoomBusiness.chatFree(imChannel, msg);
            return;
        }

        //遍历执行命令
        List<CommandStrategy> strategyList = commandInitiator.getStrategyList();
        if(strategyList != null && strategyList.size() > 0){
            String execute = commandInitiator.execute(msg, channel);
            if(StringUtils.isNotEmpty(execute)){
                channel.writeAndFlush(date + execute + "\n");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded");
        Channel channel = ctx.channel();
        int hashCode = channel.hashCode();

        IMChannel imChannel = new IMChannel();
        imChannel.setChannel(channel);
        imChannel.setHashCode(hashCode + "");
        ChannelUtils.addChannel(hashCode + "", imChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved");
        Channel channel = ctx.channel();
        int hashCode = channel.hashCode();

        ChannelUtils.removeChannel(hashCode + "");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        int hashCode = channel.hashCode();

        IMChannel imChannel = ChannelUtils.get(hashCode + "");
        if(imChannel != null){
            ChannelUtils.offline(imChannel.getName());
        }

        log.info("channelInactive");
    }
}
