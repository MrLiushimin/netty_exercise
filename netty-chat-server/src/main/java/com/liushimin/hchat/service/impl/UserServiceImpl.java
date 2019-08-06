package com.liushimin.hchat.service.impl;

import com.liushimin.hchat.mapper.TbUserMapper;
import com.liushimin.hchat.pojo.TbUser;
import com.liushimin.hchat.pojo.TbUserExample;
import com.liushimin.hchat.pojo.vo.User;
import com.liushimin.hchat.service.UserService;
import com.liushimin.hchat.utils.FastDFSClient;
import com.liushimin.hchat.utils.IdWorker;
import com.liushimin.hchat.utils.QRCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private Environment env;

    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(username);

            List<TbUser> tbUsers = userMapper.selectByExample(example);
            //校验用户存在
            if (tbUsers != null && tbUsers.size() == 1) {
                // 校验密码
                String encodePassword = DigestUtils.md5DigestAsHex(password.getBytes());
                if (encodePassword.equals(tbUsers.get(0).getPassword())) {
                    User user = new User();
                    BeanUtils.copyProperties(tbUsers.get(0), user);
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public void register(TbUser user) {
        try {
            //1.判断这个用户是否存在
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(user.getUsername());
            List<TbUser> userList = userMapper.selectByExample(example);
            if (userList != null && userList.size() > 0) {
                throw new RuntimeException("用户已经存在");
            }
            //2.将用户信息保存到数据库中
            user.setId(idWorker.nextId());
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            user.setPicSmall("");
            user.setPicNormal("");
            user.setNickname(user.getUsername());

            //生成二维码,并将二维码路径保存到数据库
            String qrcodeStr = "hichat://" + user.getUsername();
            String tempDir = env.getProperty("hcat.tmpdir");

            String qrCodeFilePath = tempDir + user.getUsername() + ".png";

            qrCodeUtils.createQRCode(qrCodeFilePath, qrcodeStr);

            //上传二维码url
            String url = env.getProperty("fdfs.httpurl") + fastDFSClient.uploadFile(new File(qrCodeFilePath));
            user.setQrcode(url);

            user.setCreatetime(new Date());
            userMapper.insert(user);
        }catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("注册失败");
        }
    }

    @Override
    public void updateNickname(String id, String nickname) {
        if (StringUtils.isNotBlank(nickname)) {
            TbUser tbUser = userMapper.selectByPrimaryKey(id);
            tbUser.setNickname(nickname);
            userMapper.updateByPrimaryKey(tbUser);
        } else {
            throw new RuntimeException("昵称不能为空");
        }
    }

    @Override
    public User findById(String userid) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userid);
        User user = new User();
        BeanUtils.copyProperties(tbUser, user);
        return user;
    }

    @Override
    public User upload(MultipartFile file, String userid) {
        try {
            //fastDFS上传文件路径,不包含ip
            String url = fastDFSClient.uploadFace(file);
            //FastDFS上传会自动生成一个缩略图
            //文件名_150x150.后缀
            String[] fileNameList = url.split("\\.");
            String fileName = fileNameList[0];
            String ext = fileNameList[1];
            String picSmallUrl = fileName + "_150x150." + ext;
            String prefix = env.getProperty("fdfs.httpurl");

            TbUser tbUser = userMapper.selectByPrimaryKey(userid);
            //设置头像大图片
            tbUser.setPicNormal(prefix + url);
            //设置头像小图片
            tbUser.setPicSmall(prefix + picSmallUrl);
            //更新到数据库
            userMapper.updateByPrimaryKey(tbUser);

            User user = new User();

            BeanUtils.copyProperties(tbUser, user);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findByUsername(String userid, String friendUsername) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(friendUsername);
        List<TbUser> friends = userMapper.selectByExample(example);
        User friendUser = new User();
        BeanUtils.copyProperties(friends.get(0), friendUser);

        return friendUser;
    }
}
