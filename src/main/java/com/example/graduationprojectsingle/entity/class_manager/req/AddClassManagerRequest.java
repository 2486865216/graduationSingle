package com.example.graduationprojectsingle.entity.class_manager.req;

import lombok.Data;

@Data
public class AddClassManagerRequest {
    /**
     * 班级名称
     */
    private String name;

    /**
     * 班级编码
     */
    private String code;

    /**
     * 所属专业id
     */
    private Long professionalId;
}
