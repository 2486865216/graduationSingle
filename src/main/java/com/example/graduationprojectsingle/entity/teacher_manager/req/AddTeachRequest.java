package com.example.graduationprojectsingle.entity.teacher_manager.req;

import lombok.Data;

@Data
public class AddTeachRequest {
    private String username;
    private String nickname;
    private Long departmentId;
}
