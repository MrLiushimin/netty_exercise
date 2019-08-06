package com.liushimin.hchat.controller;

import com.liushimin.hchat.pojo.TbFriendReq;
import com.liushimin.hchat.pojo.vo.FriendReq;
import com.liushimin.hchat.pojo.vo.Result;
import com.liushimin.hchat.pojo.vo.User;
import com.liushimin.hchat.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    //添加好友请求
    @RequestMapping("/sendRequest")
    public Result sendRequest(@RequestBody TbFriendReq friendReq) {
        try {
            friendService.sendRequest(friendReq.getFromUserid(), friendReq.getToUserid());
            return new Result(true, "已申请");
        }catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "申请好友失败");
        }
    }

    @RequestMapping("/findFriendReqByUserid")
    public List<FriendReq> findFriendReqByUserid(String userid) {
        return friendService.findFriendReqByUserid(userid);
    }

    @RequestMapping("/acceptFriendReq")
    public Result acceptFriendRed(String reqid) {
        try {
            friendService.acceptFriendRed(reqid);
            return new Result(true, "添加好友成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加好友失败");
        }
    }

    @RequestMapping("/ignoreFriendReq")
    public Result ignoreFriendRed(String reqid) {
        try {
            friendService.ignoreFriendRed(reqid);
            return new Result(true, "忽略好友成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "忽略好友失败");
        }
    }

    @RequestMapping("/findFriendByUserid")
    public List<User> findFriendByUserid(String userid) {
        System.out.println("-----------------查询好友");
        return friendService.findFriendByUserid(userid);
    }

}
