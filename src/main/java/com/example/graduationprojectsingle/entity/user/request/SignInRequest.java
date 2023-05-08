package com.example.graduationprojectsingle.entity.user.request;

import lombok.Data;

/**
 * 注册请求
 */
@Data
public class SignInRequest {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
