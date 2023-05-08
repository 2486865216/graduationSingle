package com.example.graduationprojectsingle.entity.user;

import lombok.Data;

/**
 * 解析token信息
 */
@Data
public class TokenUser {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 权限
     */
    private Integer role;
}
