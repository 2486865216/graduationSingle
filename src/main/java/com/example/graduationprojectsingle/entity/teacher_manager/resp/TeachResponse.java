package com.example.graduationprojectsingle.entity.teacher_manager.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeachResponse {
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
    /**
     * 所属院系id
     */
    private Long departmentId;

    /**
     * 所属院系名称
     */
    private String departmentName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 头像
     */
    private String avatarUrl;
}
