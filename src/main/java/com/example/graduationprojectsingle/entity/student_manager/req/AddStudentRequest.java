package com.example.graduationprojectsingle.entity.student_manager.req;

import lombok.Data;

@Data
public class AddStudentRequest {
    private String username;
    private String nickname;
    private Long classId;
}
