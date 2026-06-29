package com.demo.service;

import com.demo.mapper.UserMapper;
import com.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("123456");
        testUser.setRole("ADMIN");
        testUser.setRealName("系统管理员");
    }

    @Test
    void testFindByUsername_ShouldReturnUser_WhenUsernameExists() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        User result = userService.findByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void testFindByUsername_ShouldReturnNull_WhenUsernameNotExists() {
        when(userMapper.selectOne(any())).thenReturn(null);

        User result = userService.findByUsername("unknown");

        assertNull(result);
    }

    @Test
    void testRegister_ShouldSuccess_WhenUsernameNotExists() {
        when(userMapper.selectOne(any())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User newUser = new User();
        newUser.setUsername("test01");
        newUser.setPassword("123456");
        newUser.setRole("USER");
        newUser.setRealName("测试员");

        User result = userService.register(newUser);

        assertNotNull(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testRegister_ShouldReturnNull_WhenUsernameExists() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        User newUser = new User();
        newUser.setUsername("admin");

        User result = userService.register(newUser);

        assertNull(result);
        verify(userMapper, never()).insert(any(User.class));
    }
}