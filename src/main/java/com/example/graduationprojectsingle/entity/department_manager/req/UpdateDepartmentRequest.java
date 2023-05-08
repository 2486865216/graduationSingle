package com.example.graduationprojectsingle.entity.department_manager.req;

import lombok.Data;

@Data
public class UpdateDepartmentRequest {
    private Long id;
    private String name;
    private String code;
}
