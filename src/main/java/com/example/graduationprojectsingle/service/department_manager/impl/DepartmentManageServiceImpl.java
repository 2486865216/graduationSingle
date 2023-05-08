package com.example.graduationprojectsingle.service.department_manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.department_manager.DepartmentManagerMapper;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
import com.example.graduationprojectsingle.service.curriculum_manager.CurriculumManagerService;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.service.professional_manager.ProfessionalManagerService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DepartmentManageServiceImpl extends ServiceImpl<DepartmentManagerMapper, DepartmentManager> implements DepartmentManagerService {

    @Autowired
    @Lazy
    private ProfessionalManagerService professionalManagerService;

    @Autowired
    @Lazy
    private ClassManagerService classManagerService;

    @Autowired
    @Lazy
    private CurriculumManagerService curriculumManagerService;

    @Autowired
    @Lazy
    private LoginService loginService;

    // 查询所有院系
    @Override
    public List<DepartmentManager> getList(String name, String code) {
        LambdaQueryWrapper<DepartmentManager> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(DepartmentManager::getName, name);
        }
        if (StringUtils.isNotEmpty(code)) {
            queryWrapper.like(DepartmentManager::getCode, code);
        }
        queryWrapper.eq(DepartmentManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    // 新增院系
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDepartment(String name, String code) {
        // 检查名称是否重复
        LambdaQueryWrapper<DepartmentManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(DepartmentManager::getName, name)
                .eq(DepartmentManager::getIsDelete, false);
        List<DepartmentManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NAME_REPEAT);
        }

        // 检查编码是否重复
        LambdaQueryWrapper<DepartmentManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(DepartmentManager::getCode, code)
                .eq(DepartmentManager::getIsDelete, false);
        List<DepartmentManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_CODE_REPEAT);
        }

        DepartmentManager departmentManager = new DepartmentManager();
        departmentManager.setName(name);
        departmentManager.setCode(code);
        departmentManager.setCreateTime(LocalDateTime.now());
        departmentManager.setUpdateTime(LocalDateTime.now());

        return this.save(departmentManager);
    }

    // 修改院系
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(Long id, String name, String code) {
        LambdaQueryWrapper<DepartmentManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentManager::getId, id)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentManager = this.getOne(queryWrapper);
        if (Objects.isNull(departmentManager)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NAME_REPEAT);
        }

        // 检查名称是否重复
        LambdaQueryWrapper<DepartmentManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(DepartmentManager::getName, name)
                .eq(DepartmentManager::getIsDelete, false);
        List<DepartmentManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            names.removeIf(item -> item.getId().equals(id));
            if (!names.isEmpty()) {
                throw new ServiceException(ConsumerException.DEPARTMENT_NAME_REPEAT);
            }
        }

        // 检查编码是否重复
        LambdaQueryWrapper<DepartmentManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(DepartmentManager::getCode, code)
                .eq(DepartmentManager::getIsDelete, false);
        List<DepartmentManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            codes.removeIf(item -> item.getId().equals(id));
            if (!codes.isEmpty()) {
                throw new ServiceException(ConsumerException.DEPARTMENT_CODE_REPEAT);
            }
        }

        departmentManager.setName(name);
        departmentManager.setCode(code);
        departmentManager.setUpdateTime(LocalDateTime.now());

        if (this.updateById(departmentManager)) {
            return professionalManagerService.updateDepartment(departmentManager.getId(), departmentManager.getName())
                    && classManagerService.updateDepartment(departmentManager.getId(), departmentManager.getName())
                    && curriculumManagerService.updateDepartment(departmentManager.getId(), departmentManager.getName());
        }
        return false;
    }

    // 删除院系
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDepartment(Long id) {
        LambdaQueryWrapper<DepartmentManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentManager::getId, id)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentManager = this.getOne(queryWrapper);
        if (Objects.isNull(departmentManager)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NAME_REPEAT);
        }

        departmentManager.setIsDelete(true);
        departmentManager.setUpdateTime(LocalDateTime.now());

        if (this.updateById(departmentManager)) {
            return professionalManagerService.deleteProfessionalByDepartmentId(departmentManager.getId())
                    && classManagerService.deleteClassByDepartmentId(departmentManager.getId())
                    && curriculumManagerService.deleteCurriculumByDepartmentId(departmentManager.getId())
                    && loginService.delUserByDepartmentId(departmentManager.getId());
        }

        return false;
    }

    @Override
    public DepartmentManager getDepartmentById(Long id) {
        LambdaQueryWrapper<DepartmentManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentManager::getId, id)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager one = this.getOne(queryWrapper);
        if (one == null) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NOT_EXIST);
        }
        return one;
    }
}
