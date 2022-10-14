package com.junlin.controller;

import com.junlin.repository.entity.User;
import com.junlin.repository.service.UserService;
import com.junlin.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String test() {

        System.out.println(redisUtil.set("test", "Hello"));
        System.out.println(redisUtil.get("test"));

        User user = userService.getById(1);
        System.out.println(user);

        return "Hello";
    }
}
