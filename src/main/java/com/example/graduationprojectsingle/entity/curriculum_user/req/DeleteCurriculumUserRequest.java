package com.example.graduationprojectsingle.entity.curriculum_user.req;

import lombok.Data;

@Data
public class DeleteCurriculumUserRequest {
    private Long userId;
    private Long curriculumId;
}
