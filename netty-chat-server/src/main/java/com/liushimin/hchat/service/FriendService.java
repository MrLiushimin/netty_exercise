package com.liushimin.hchat.service;

import com.liushimin.hchat.pojo.vo.FriendReq;
import com.liushimin.hchat.pojo.vo.User;

import java.util.List;

public interface FriendService {
    void sendRequest(String fromUserid, String toUserid);

    List<FriendReq> findFriendReqByUserid(String userid);

    void acceptFriendRed(String reqid);

    void ignoreFriendRed(String reqid);

    List<User> findFriendByUserid(String userid);
}
