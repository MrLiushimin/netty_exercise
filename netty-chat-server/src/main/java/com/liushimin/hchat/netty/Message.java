package com.liushimin.hchat.netty;

import com.liushimin.hchat.pojo.TbChatRecord;

public class Message {
    private Integer type; //消息类型
    private TbChatRecord chatRecord; //聊天消息
    private Object ext; //扩展消息字段

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", chatRecord=" + chatRecord +
                ", ext=" + ext +
                '}';
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public TbChatRecord getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(TbChatRecord chatRecord) {
        this.chatRecord = chatRecord;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

}
