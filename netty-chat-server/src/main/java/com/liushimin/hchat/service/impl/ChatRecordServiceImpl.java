package com.liushimin.hchat.service.impl;

import com.liushimin.hchat.mapper.TbChatRecordMapper;
import com.liushimin.hchat.pojo.TbChatRecord;
import com.liushimin.hchat.pojo.TbChatRecordExample;
import com.liushimin.hchat.service.ChatRecordService;
import com.liushimin.hchat.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>标题: </p>
 * <p>功能描述: 聊天记录服务类接口实现</p>
 *
 * <p>创建时间: 2019/8/2 17:55</p>
 * <p>作者：lshim</p>
 * <p>修改历史记录：</p>
 * ====================================================================<br>
 */
@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private TbChatRecordMapper chatRecordMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void insert(TbChatRecord chatRecord) {
        chatRecord.setId(idWorker.nextId());
        chatRecord.setCreatetime(new Date());
        chatRecord.setHasDelete(0);
        chatRecord.setHasRead(0);

        chatRecordMapper.insert(chatRecord);
    }

    @Override
    public void updateStatusHasRead(String id) {
        TbChatRecord chatRecord = chatRecordMapper.selectByPrimaryKey(id);
        chatRecord.setHasRead(1);

        chatRecordMapper.updateByPrimaryKey(chatRecord);
    }

    @Override
    public List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid) {
        TbChatRecordExample chatRecordExample = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria1 = chatRecordExample.createCriteria();
        TbChatRecordExample.Criteria criteria2 = chatRecordExample.createCriteria();

        //用户发给朋友的
        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasReadEqualTo(0);

        //朋友发给用户的
        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasReadEqualTo(0);

        chatRecordExample.or(criteria1);
        chatRecordExample.or(criteria2);

        //将朋友发送给用户的记录置为已读
        TbChatRecordExample exampleQuerySendToMe = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteriaSendToMe = exampleQuerySendToMe.createCriteria();
        criteriaSendToMe.andHasReadEqualTo(0);
        criteriaSendToMe.andFriendidEqualTo(userid);

        List<TbChatRecord> tbChatRecords = chatRecordMapper.selectByExample(exampleQuerySendToMe);

        for (TbChatRecord tbChatRecord : tbChatRecords) {
            tbChatRecord.setHasRead(1);
            chatRecordMapper.updateByPrimaryKey(tbChatRecord);
        }

        return chatRecordMapper.selectByExample(chatRecordExample);
    }

    @Override
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = example.createCriteria();
        //查询发送给用户的未读消息
        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);

        return chatRecordMapper.selectByExample(example);
    }
}
