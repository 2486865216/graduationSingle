package com.example.graduationprojectsingle.service.curriculum_manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.curriculum_manager.CurriculumManager;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.AddCurriculumRequest;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.UpdateCurriculumRequest;

import java.util.List;

public interface CurriculumManagerService extends IService<CurriculumManager> {

    List<CurriculumManager> getList(String search, String name, String code, Double credit, Integer grade, Long departmentId);

    List<CurriculumManager> getByTeacherId(Long teacherId);

    List<CurriculumManager> getByClassRoomId(Long classRoomId);

    boolean addCurriculum(AddCurriculumRequest request);

    boolean updateCurriculum(UpdateCurriculumRequest request);

    boolean deleteCurriculumManager(Long id);

    //更改院系信息
    boolean updateDepartment(Long id, String name);

    //根据院校id删除
    boolean deleteCurriculumByDepartmentId(Long id);

    //更改教师信息
    boolean updateTeacher(Long id, String name);

    List<CurriculumManager> getByUserId(Long userId);
}
