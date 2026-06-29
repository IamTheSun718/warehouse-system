package com.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mapper.UserMapper;
import com.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    public User register(User user) {
        if (findByUsername(user.getUsername()) != null) {
            return null;
        }
        userMapper.insert(user);
        return user;
    }

    public User findById(Long id) {
        return userMapper.selectById(id);
    }
}