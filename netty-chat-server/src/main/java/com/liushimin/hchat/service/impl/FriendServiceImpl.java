package com.liushimin.hchat.service.impl;

import com.liushimin.hchat.mapper.TbFriendMapper;
import com.liushimin.hchat.mapper.TbFriendReqMapper;
import com.liushimin.hchat.mapper.TbUserMapper;
import com.liushimin.hchat.pojo.*;
import com.liushimin.hchat.pojo.vo.FriendReq;
import com.liushimin.hchat.pojo.vo.User;
import com.liushimin.hchat.service.FriendService;
import com.liushimin.hchat.utils.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    @Autowired
    private TbFriendReqMapper friendReqMapper;

    @Autowired
    private TbFriendMapper friendMapper;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private IdWorker idWorker;
    
    @Override
    public void sendRequest(String fromUserid, String toUserid) {
        //检查是否允许添加好友
        TbUser friend = userMapper.selectByPrimaryKey(toUserid);

        checkAllowAddFriend(fromUserid, friend);
        
        TbFriendReq friendReq = new TbFriendReq();
        friendReq.setId(idWorker.nextId());
        friendReq.setCreatetime(new Date());
        friendReq.setFromUserid(fromUserid);
        friendReq.setToUserid(toUserid);
        friendReq.setStatus(0);

        friendReqMapper.insert(friendReq);
    }

    private void checkAllowAddFriend(String userid, TbUser friend) {
        //1.用户不能添加自己为好友
        if (friend.getId().equals(userid)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        TbFriendExample friendExample = new TbFriendExample();

        //已经添加过的不能重复添加
        TbFriendExample.Criteria friendCriteria = friendExample.createCriteria();
        friendCriteria.andUseridEqualTo(userid);
        friendCriteria.andFriendsIdEqualTo(friend.getId());
        List<TbFriend> friends = friendMapper.selectByExample(friendExample);
        if (friends != null && friends.size() > 0) {
            throw new RuntimeException("已经是您的好友了");
        }

        //已经提交过申请的不能再次提交申请
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria = friendReqExample.createCriteria();
        friendReqCriteria.andFromUseridEqualTo(userid);
        friendReqCriteria.andToUseridEqualTo(friend.getId());
        //请求为处理的
        friendReqCriteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqs = friendReqMapper.selectByExample(friendReqExample);

        if (friendReqs != null && friendReqs.size() > 0) {
            throw new RuntimeException("已经申请过了");
        }

    }

    @Override
    public List<FriendReq> findFriendReqByUserid(String userid) {
        //根据用户的id查询对应的好友请求
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria = friendReqExample.createCriteria();

        friendReqCriteria.andToUseridEqualTo(userid);
        friendReqCriteria.andStatusEqualTo(0);
        List<TbFriendReq> tbFriendReqs = friendReqMapper.selectByExample(friendReqExample);

        ArrayList<FriendReq> friendReqs = new ArrayList<>();

        for (TbFriendReq tbFriendReq : tbFriendReqs) {
            TbUser tbUser = userMapper.selectByPrimaryKey(tbFriendReq.getFromUserid());
            FriendReq friendReq = new FriendReq();
            BeanUtils.copyProperties(tbUser, friendReq);
            friendReq.setId(tbFriendReq.getId());

            friendReqs.add(friendReq);
        }

        return friendReqs;
    }

    @Override
    public void acceptFriendRed(String reqid) {
        //将好友请求的status设置为1,表示已经处理了该好友请求
        TbFriendReq tbFriendReq = friendReqMapper.selectByPrimaryKey(reqid);
        tbFriendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKey(tbFriendReq);

        //互相添加好友
        TbFriend friend1 = new TbFriend();
        friend1.setId(idWorker.nextId());
        friend1.setCreatetime(new Date());
        friend1.setUserid(tbFriendReq.getFromUserid());
        friend1.setFriendsId(tbFriendReq.getToUserid());

        TbFriend friend2 = new TbFriend();
        friend2.setId(idWorker.nextId());
        friend2.setCreatetime(new Date());
        friend2.setUserid(tbFriendReq.getToUserid());
        friend2.setFriendsId(tbFriendReq.getFromUserid());

        friendMapper.insert(friend1);
        friendMapper.insert(friend2);
    }

    @Override
    public void ignoreFriendRed(String reqid) {
        TbFriendReq tbFriendReq = friendReqMapper.selectByPrimaryKey(reqid);
        tbFriendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKey(tbFriendReq);
    }

    @Override
    public List<User> findFriendByUserid(String userid) {
        //查出所有的friend
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria criteria = friendExample.createCriteria();
        criteria.andUseridEqualTo(userid);

        List<TbFriend> tbFriends = friendMapper.selectByExample(friendExample);
        List<User> friends = new ArrayList<>();
        //将friend记录转换为user返回
        for (TbFriend friend : tbFriends) {
            TbUser tbUser = userMapper.selectByPrimaryKey(friend.getFriendsId());
            User user = new User();
            BeanUtils.copyProperties(tbUser, user);

            friends.add(user);
        }


        return friends;
    }
}
