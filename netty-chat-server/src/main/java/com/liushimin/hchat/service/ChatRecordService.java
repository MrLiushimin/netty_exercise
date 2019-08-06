package com.liushimin.hchat.service;

import com.liushimin.hchat.pojo.TbChatRecord;

import java.util.List;

/**
 * <p>标题: </p>
 * <p>功能描述: 聊天记录服务类接口/p>
 *
 * <p>创建时间: 2019/8/2 17:54</p>
 * <p>作者：lshim</p>
 * <p>修改历史记录：</p>
 * ====================================================================<br>
 */
public interface ChatRecordService {
    void insert(TbChatRecord chatRecord);

    void updateStatusHasRead(String id);

    List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid);

    List<TbChatRecord> findUnreadByUserid(String userid);
}
