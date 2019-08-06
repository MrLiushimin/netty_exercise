package com.liushimin.hchat.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>标题: </p>
 * <p>功能描述: 建立用户ID与通道的关联</p>
 *
 * <p>创建时间: 2019/8/2 17:00</p>
 * <p>作者：lshim</p>
 * <p>修改历史记录：</p>
 * ====================================================================<br>
 */
public class UserChannelMap {

    //保存用户id与channel通道的对应关系
    private static Map<String, Channel> userChannelMap;

    static {
        userChannelMap = new HashMap<String, Channel>();
    }


    /**
     * @Description: 添加用户id与channel的关联
     * @Author: lshim on 2019/8/2 17:10
     * @param: [userid, channel]
     * @return: void
     */
    public static void put(String userid, Channel channel) {
        userChannelMap.put(userid, channel);
    }

    /**
     * @Description: 根据通道id一处通道关联
     * @Author: lshim on 2019/8/2 17:12
     * @param: [userid]
     * @return: void
     */
    public static void remove(String userid) {
        userChannelMap.remove(userid);
    }

    /**
     * @Description: 根据好友id获取对应的通道
     * @Author: lshim on 2019/8/2 17:13
     * @param: [friendid]
     * @return: io.netty.channel.Channel
     */
    public static Channel get(String friendid) {
        return userChannelMap.get(friendid);
    }

    /**
     * @Description: 打印所有的用户与通道的关联数据
     * @Author: lshim on 2019/8/2 17:15
     * @param: []
     * @return: void
     */
    public static void print() {
        for (String s : userChannelMap.keySet()) {
            System.out.println("用户id:" + s + "\t通道:" + userChannelMap.get(s).id());
        }
    }

    /**
     * @Description: 根据通道id一处用户与channel的关联
     * @Author: lshim on 2019/8/2 17:20
     * @param: [channelId]
     * @return: void
     */
    public static void removeByChannelId(String channelId) {
        if (!StringUtils.isNotBlank(channelId)) {
            return;
        }

        for (String s : userChannelMap.keySet()) {
            Channel channel = userChannelMap.get(s);
            if (channelId.equals(channel.id().asLongText())) {
                System.out.println("客户端连接断开,取消用户:" + s + "\t与通道" + channelId + "的关联");
                userChannelMap.remove(s);
                break;
            }
        }
    }

}
