package com.example.graduationprojectsingle.service.department_manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;

import java.util.List;

public interface DepartmentManagerService extends IService<DepartmentManager> {

    //查询所有院系
    List<DepartmentManager> getList(String name, String code);

    //新增院系
    boolean addDepartment(String name, String code);

    //修改院系
    boolean updateDepartment(Long id, String name, String code);

    //删除院系
    boolean deleteDepartment(Long id);

    DepartmentManager getDepartmentById(Long id);
}
