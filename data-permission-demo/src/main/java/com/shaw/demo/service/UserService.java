package com.shaw.demo.service;

import com.github.pagehelper.PageHelper;
import com.shaw.demo.entity.User;
import com.shaw.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 *
 * @author wangchuang
 * @date 2021/6/14 12:53:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;


    /**
     * 获取用户详情
     *
     * @param userId 用户id
     */
    public User getDetail(Integer userId) {

        PageHelper.startPage(1, 10);
        // 获取用户信息
        User user = userMapper.selectById(userId);


        return user;
    }


    /**
     * 数据状态查询用户列表
     *
     * @param state 查询条件
     */
    public List<User> getList(Integer state) {

        // 获取用户表信息
        List<User> userList = userMapper.getUserByState(state);
        return userList;
    }


}