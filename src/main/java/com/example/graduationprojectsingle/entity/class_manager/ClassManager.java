package com.example.graduationprojectsingle.entity.class_manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassManager {
    /**
     * id
     */
    private Long id;

    /**
     * 班级名称
     */
    private String name;

    /**
     * 班级编码
     */
    private String code;

    /**
     * 所属院系id
     */
    private Long departmentId;

    /**
     * 所属院系名称
     */
    private String departmentName;

    /**
     * 所属专业id
     */
    private Long professionalId;

    /**
     * 所属专业名称
     */
    private String professionalName;

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
