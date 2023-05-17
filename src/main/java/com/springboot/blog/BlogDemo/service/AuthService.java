package com.springboot.blog.BlogDemo.service;

import com.springboot.blog.BlogDemo.payload.LoginDto;
import com.springboot.blog.BlogDemo.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}

