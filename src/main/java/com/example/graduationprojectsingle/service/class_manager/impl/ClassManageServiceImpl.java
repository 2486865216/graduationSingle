package com.example.graduationprojectsingle.service.class_manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;
import com.example.graduationprojectsingle.entity.professional_manager.ProfessionalManager;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.class_manager.ClassManagerMapper;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
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
public class ClassManageServiceImpl extends ServiceImpl<ClassManagerMapper, ClassManager> implements ClassManagerService {

    @Autowired
    private ProfessionalManagerService professionalManagerService;

    @Autowired
    @Lazy
    private LoginService loginService;

    @Override
    public List<ClassManager> getList(String search, String name, String code, Long departmentId, Long professionalId) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(ClassManager::getName, name);
        }
        if (StringUtils.isNotEmpty(code)) {
            queryWrapper.like(ClassManager::getCode, code);
        }
        if (Objects.nonNull(departmentId)) {
            queryWrapper.eq(ClassManager::getDepartmentId, departmentId);
        }
        if (Objects.nonNull(professionalId)) {
            queryWrapper.eq(ClassManager::getProfessionalId, professionalId);
        }
        if (StringUtils.isNotEmpty(search)) {
            queryWrapper.and(qw -> {
                qw.or().like(ClassManager::getName, search)
                        .or().like(ClassManager::getCode, search)
                        .or().like(ClassManager::getProfessionalName, search)
                        .or().like(ClassManager::getDepartmentName, search);
            });
        }
        queryWrapper.eq(ClassManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addClassManager(String name, String code, Long professionalId) {
        LambdaQueryWrapper<ClassManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(ClassManager::getName, name)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            throw new ServiceException(ConsumerException.CLASS_NAME_REPEAT);
        }

        LambdaQueryWrapper<ClassManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(ClassManager::getCode, code)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            throw new ServiceException(ConsumerException.CLASS_CODE_REPEAT);
        }

        LambdaQueryWrapper<ProfessionalManager> queryWrapperProfessionalId = new LambdaQueryWrapper<>();
        queryWrapperProfessionalId.eq(ProfessionalManager::getId, professionalId)
                .eq(ProfessionalManager::getIsDelete, false);
        ProfessionalManager professionalManager = professionalManagerService.getOne(queryWrapperProfessionalId);
        if (Objects.isNull(professionalManager)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_NOT_EXIST);
        }

        ClassManager classManager = new ClassManager();
        classManager.setName(name);
        classManager.setCode(code);
        classManager.setDepartmentId(professionalManager.getDepartmentId());
        classManager.setDepartmentName(professionalManager.getDepartmentName());
        classManager.setProfessionalId(professionalId);
        classManager.setProfessionalName(professionalManager.getName());
        classManager.setCreateTime(LocalDateTime.now());
        classManager.setUpdateTime(LocalDateTime.now());

        return this.save(classManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateClassManager(Long id, String name, String code, Long professionalId) {
        LambdaQueryWrapper<ClassManager> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(ClassManager::getId, id)
                .eq(ClassManager::getIsDelete, false);
        ClassManager classManager = this.getOne(queryWrapperById);
        if (Objects.isNull(classManager)) {
            throw new ServiceException(ConsumerException.CLASS_NOT_EXIST);
        }

        LambdaQueryWrapper<ClassManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(ClassManager::getName, name)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            names.removeIf(item -> item.getId().equals(id));
            if (!names.isEmpty()) {
                throw new ServiceException(ConsumerException.CLASS_NAME_REPEAT);
            }
        }

        LambdaQueryWrapper<ClassManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(ClassManager::getCode, code)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            codes.removeIf(item -> item.getId().equals(id));
            if (!codes.isEmpty()) {
                throw new ServiceException(ConsumerException.CLASS_CODE_REPEAT);
            }
        }

        LambdaQueryWrapper<ProfessionalManager> queryWrapperProfessionalId = new LambdaQueryWrapper<>();
        queryWrapperProfessionalId.eq(ProfessionalManager::getId, professionalId)
                .eq(ProfessionalManager::getIsDelete, false);
        ProfessionalManager professionalManager = professionalManagerService.getOne(queryWrapperProfessionalId);
        if (Objects.isNull(professionalManager)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_NOT_EXIST);
        }

        classManager.setName(name);
        classManager.setCode(code);
        classManager.setDepartmentId(professionalManager.getDepartmentId());
        classManager.setDepartmentName(professionalManager.getDepartmentName());
        classManager.setProfessionalId(professionalId);
        classManager.setProfessionalName(professionalManager.getName());
        classManager.setUpdateTime(LocalDateTime.now());

        return this.updateById(classManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClassManager(Long id) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassManager::getId, id)
                .eq(ClassManager::getIsDelete, false);
        ClassManager classManager = this.getOne(queryWrapper);
        if (Objects.isNull(classManager)) {
            throw new ServiceException(ConsumerException.CLASS_NOT_EXIST);
        }

        classManager.setIsDelete(true);
        classManager.setUpdateTime(LocalDateTime.now());

        return this.updateById(classManager) && loginService.delUserByClassId(id);
    }

    //更改院系信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(Long id, String name) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassManager::getDepartmentId, id)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ClassManager classManager : list) {
                classManager.setDepartmentId(id);
                classManager.setDepartmentName(name);
                classManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    //根据院校id删除专业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClassByDepartmentId(Long id) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassManager::getDepartmentId, id)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ClassManager classManager : list) {
                classManager.setIsDelete(true);
                classManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public boolean updateProfessional(Long id, String name) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassManager::getProfessionalId, id)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ClassManager classManager : list) {
                classManager.setProfessionalId(id);
                classManager.setProfessionalName(name);
                classManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public boolean deleteClassByProfessionalId(Long id) {
        LambdaQueryWrapper<ClassManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassManager::getProfessionalId, id)
                .eq(ClassManager::getIsDelete, false);
        List<ClassManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ClassManager classManager : list) {
                classManager.setIsDelete(true);
                classManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }
}
