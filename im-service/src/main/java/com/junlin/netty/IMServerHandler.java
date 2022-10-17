package com.junlin.netty;

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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

        //遍历执行命令
        List<CommandStrategy> strategyList = commandInitiator.getStrategyList();
        if(strategyList != null && strategyList.size() > 0){
            String execute = commandInitiator.execute(msg, channel);
            if(StringUtils.isNotEmpty(execute)){
                channel.writeAndFlush(date + execute + "\n");
            }
        }


        /*if(msg.startsWith("auth")){
            String[] arr = msg.split(" ");
            if(arr != null && arr.length == 2){
                imChannel.setName(arr[1]);
                imChannel.setChannel(channel);
                imChannel.setHashCode(hashCode+"");
                ChannelUtils.addChannel(hashCode + "", imChannel);
                this.online(imChannel.getName(), hashCode+"");

                channel.writeAndFlush("[" + sdf.format(new java.util.Date()) + "]" + "认证成功 \n");
                return;
            }
        }

        if(msg.startsWith("send")){
            String[] arr = msg.split(" ");
            if(arr != null && arr.length == 3){
                Object target = redisUtil.get("USERINFO:"+arr[1]);
                if(target == null){
                    //todo 不在线
                    log.info(arr[1] + "不在线");
                    channel.writeAndFlush("[" + sdf.format(new java.util.Date()) + "-" + arr[1] + "]" + "不在线 \n");
                }else{
                    UserInfo userInfo = (UserInfo) target;
                    //todo 省略MQ
                    IMChannel targetChannel = ChannelUtils.get(userInfo.getHashCode());
                    if(targetChannel != null){
                        targetChannel.getChannel().writeAndFlush(sdf.format(new java.util.Date()) + "FROM " + imChannel.getName() + "：" + arr[2]);
                    }
                }
            }
        }*/
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
