package com.example.graduationprojectsingle.entity.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 权限
     */
    private Integer role;

    /**
     * 所属班级id
     */
    private Long classId;

    /**
     * 所属专业id
     */
    private Long professionalId;

    /**
     * 所属院系id
     */
    private Long departmentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 头像
     */
    private String avatarUrl;
}
