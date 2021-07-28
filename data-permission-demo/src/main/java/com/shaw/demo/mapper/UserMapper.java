package com.shaw.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shaw.demo.config.DataAuth;
import com.shaw.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    @DataAuth
    List<User> getUserByState( Integer state);

}
