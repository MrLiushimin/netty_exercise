package com.liushimin.hchat.netty;

import com.alibaba.fastjson.JSON;
import com.liushimin.hchat.pojo.TbChatRecord;
import com.liushimin.hchat.service.ChatRecordService;
import com.liushimin.hchat.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理消息的handler
 * TextWebSocketFrame: 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用来保存所有的客户端连接
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM");

    /**
     * @Description: 当有新的客户端连接服务器之后,会自动调用这个方法
     * @Author: lshim on 2019/8/2 17:35
     * @param: [ctx]
     * @return: void
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //将新的通道加入到clients
        clients.add(ctx.channel());
    }


    /**
     * @Description: 接受到消息之后自动调用,根据不同的消息类型进行不同的动作
     *              type-0:建立连接
     *                   1:发送消息
     *                   2:签收消息
     *                   3:接收心跳消息
     * @Author: lshim on 2019/8/2 17:41
     * @param: [ctx, msg]
     * @return: void
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //接收到数据后自动调用
        //获取客户端发送过来的文本消息
        String text = msg.text();
        System.out.println("接收到的消息数据为:" + text);

        Message message = JSON.parseObject(text, Message.class);

        ChatRecordService chatRecordService = SpringUtil.getBean(ChatRecordService.class);

        switch (message.getType()) {
            //客户端连接
            case 0:
                //建立用户与通道的关联
                String userid = message.getChatRecord().getUserid();
                UserChannelMap.put(userid, ctx.channel());
                System.out.println("建立用户:" + userid + "\t与通道:" + ctx.channel().id().asLongText() + "\t的关联");
                UserChannelMap.print();
                break;
            //客户端消息发送
            case 1:
                System.out.println("接收到用户的消息" + JSON.toJSONString(message));
                TbChatRecord chatRecord = message.getChatRecord();
                chatRecordService.insert(chatRecord);

                Channel channel = UserChannelMap.get(chatRecord.getFriendid());
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                } else {
                    //如果不在线,暂时不发送
                    System.out.println("用户" + chatRecord.getFriendid() + "\t不在线");
                }
                break;
            //客户端消息接收
            case 2:
                chatRecordService.updateStatusHasRead(message.getChatRecord().getId());
                break;
            //保持心跳
            case 3:
                System.out.println("接收到心跳消息" + JSON.toJSONString(message));
                break;
        }


    }

    /**
     * @Description: 关闭通道是,根据通道id一处与用户的关联
     * @Author: lshim on 2019/8/2 17:33
     * @param: [ctx]
     * @return: void
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭通道");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        UserChannelMap.print();
    }

    /**
     * @Description: 发生异常时,断开连接,并移除用户与通道的关联
     * @Author: lshim on 2019/8/2 17:34
     * @param: [ctx, cause]
     * @return: void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
    }


}
