package com.shaw.demo.controller;

import com.shaw.demo.entity.User;
import com.shaw.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户模块
 *
 * @author wangchuang
 * @date 2021/6/8 16:41:43
 */
@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;


    @GetMapping("/getDetail")
    public User getDetail(@RequestParam("id") Integer id) {

        return userService.getDetail(id);
    }


    @GetMapping("/getList")
    public List<User> getList(@RequestParam("state") Integer state) {

        return userService.getList(state);
    }

    @GetMapping("/test")
    public List<User> getTest(@RequestParam("state") Integer state) {

        return userService.getTest(state);
    }


}
