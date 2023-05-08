package com.example.graduationprojectsingle.entity.class_manager.req;

import lombok.Data;

@Data
public class UpdateClassManagerRequest {
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
     * 所属专业id
     */
    private Long professionalId;
}
