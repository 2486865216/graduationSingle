package com.example.graduationprojectsingle.entity.professional_manager.req;

import lombok.Data;

@Data
public class AddProfessionalManagerRequest {
    /**
     * 专业名称
     */
    private String name;

    /**
     * 专业编码
     */
    private String code;

    /**
     * 所属院系id
     */
    private Long departmentId;
}
