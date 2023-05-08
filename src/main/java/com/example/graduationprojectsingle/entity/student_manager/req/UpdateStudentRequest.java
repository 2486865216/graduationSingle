package com.example.graduationprojectsingle.entity.student_manager.req;

import lombok.Data;

@Data
public class UpdateStudentRequest {
    private Long id;
    private String username;
    private String nickname;
    private Long classId;
}
