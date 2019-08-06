package com.liushimin.hchat.service;

import com.liushimin.hchat.pojo.TbUser;
import com.liushimin.hchat.pojo.vo.Result;
import com.liushimin.hchat.pojo.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    // 返回所有用户
    List<TbUser> findAll();

    //登录
    User login(String username, String password);

    void register(TbUser user);

    void updateNickname(String id, String nickname);

    User findById(String userId);

    User upload(MultipartFile file, String userid);

    User findByUsername(String userid, String friendUsername);
}
