package com.example.graduationprojectsingle.service.professional_manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.professional_manager.ProfessionalManager;

import java.util.List;

public interface ProfessionalManagerService extends IService<ProfessionalManager> {

    //查询所有专业
    List<ProfessionalManager> getList(String search, String name, String code, Long departmentId);

    //新增专业
    boolean addProfessional(String name, String code, Long departmentId);

    //修改专业
    boolean updateProfessional(Long id, String name, String code, Long departmentId);

    //删除专业
    boolean deleteProfessional(Long id);

    //更改院系信息
    boolean updateDepartment(Long id, String name);

    //根据院校id删除专业
    boolean deleteProfessionalByDepartmentId(Long id);
}
