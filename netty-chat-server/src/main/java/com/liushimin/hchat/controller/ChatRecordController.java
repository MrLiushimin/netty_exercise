package com.liushimin.hchat.controller;

import com.liushimin.hchat.pojo.TbChatRecord;
import com.liushimin.hchat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>标题: </p>
 * <p>功能描述: 关于聊天记录的Controller</p>
 *
 * <p>创建时间: 2019/8/5 10:42</p>
 * <p>作者：lshim</p>
 * <p>修改历史记录：</p>
 * ====================================================================<br>
 */
@RestController
@RequestMapping("/chatrecord")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;

    //查询所有的与朋友的对话记录
    @RequestMapping("/findByUserIdAndFriendId")
    public List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid) {
        try {
            return chatRecordService.findByUserIdAndFriendId(userid, friendid);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<TbChatRecord>();
        }
    }

    //查找与朋友之间未读的记录
    @RequestMapping("/findUnreadByUserid")
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        try {
            return chatRecordService.findUnreadByUserid(userid);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<TbChatRecord>();
        }
    }
}
