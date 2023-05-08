package com.example.graduationprojectsingle.service.curriculum_user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.curriculum_user.CurriculumUser;

import java.util.List;

public interface CurriculumUserService extends IService<CurriculumUser> {
    boolean addCurriculumUser(Long userId, Long curriculumId);
    boolean deleteCurriculumUserByUserId(Long userId);
    boolean deleteCurriculumUserByCurriculumId(Long curriculumId);

    List<CurriculumUser> getList(Long userId, Long curriculumId);

    boolean deleteCurriculumUserByUserIdAndCurriculumId(Long userId, Long curriculumId);

    List<Long> getByUserId(Long userId);
}
