package com.example.graduationprojectsingle.entity.department_manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentManager {
    /**
     * id
     */
    private Long id;

    /**
     * 院系名称
     */
    private String name;

    /**
     * 院系编码
     */
    private String code;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;
}
