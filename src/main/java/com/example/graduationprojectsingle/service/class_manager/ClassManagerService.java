package com.example.graduationprojectsingle.service.class_manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;

import java.util.List;

public interface ClassManagerService extends IService<ClassManager> {

    //查询所有课程
    List<ClassManager> getList(String search, String name, String code, Long departmentId, Long professionalId);

    //新增课程
    boolean addClassManager(String name, String code, Long professionalId);

    //修改课程
    boolean updateClassManager(Long id, String name, String code, Long professionalId);

    //删除课程
    boolean deleteClassManager(Long id);

    //更改院系信息
    boolean updateDepartment(Long id, String name);

    //根据院校id删除专业
    boolean deleteClassByDepartmentId(Long id);

    //更改专业信息
    boolean updateProfessional(Long id, String name);

    //根据专业id删除专业
    boolean deleteClassByProfessionalId(Long id);
}
