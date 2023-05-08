package com.example.graduationprojectsingle.service.professional_manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.professional_manager.ProfessionalManager;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.professional_manager.ProfessionalManagerMapper;
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
public class ProfessionalManageServiceImpl extends ServiceImpl<ProfessionalManagerMapper, ProfessionalManager> implements ProfessionalManagerService {

    @Autowired
    private DepartmentManagerService departmentManagerService;

    @Autowired
    @Lazy
    private ClassManagerService classManagerService;

    @Autowired
    @Lazy
    private CurriculumManagerService curriculumManagerService;

    @Autowired
    @Lazy
    private LoginService loginService;

    @Override
    public List<ProfessionalManager> getList(String search, String name, String code, Long departmentId) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(ProfessionalManager::getName, name);
        }
        if (StringUtils.isNotEmpty(code)) {
            queryWrapper.like(ProfessionalManager::getCode, code);
        }
        if (Objects.nonNull(departmentId)) {
            queryWrapper.eq(ProfessionalManager::getDepartmentId, departmentId);
        }
        queryWrapper.eq(ProfessionalManager::getIsDelete, false);
        if (StringUtils.isNotEmpty(search)) {
            queryWrapper.and(qw -> {
                qw.or().like(ProfessionalManager::getName, search)
                        .or().like(ProfessionalManager::getCode, search)
                        .or().like(ProfessionalManager::getDepartmentName, search);
            });
        }
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProfessional(String name, String code, Long departmentId) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(ProfessionalManager::getName, name)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_NAME_REPEAT);
        }

        LambdaQueryWrapper<ProfessionalManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(ProfessionalManager::getCode, code)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_CODE_REPEAT);
        }

        LambdaQueryWrapper<DepartmentManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(DepartmentManager::getId, departmentId)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentInfo = departmentManagerService.getOne(queryWrapperId);
        if (Objects.isNull(departmentInfo)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NOT_EXIST);
        }

        ProfessionalManager professionalManager = new ProfessionalManager();
        professionalManager.setName(name);
        professionalManager.setCode(code);
        professionalManager.setDepartmentId(departmentId);
        professionalManager.setDepartmentName(departmentInfo.getName());
        professionalManager.setCreateTime(LocalDateTime.now());
        professionalManager.setUpdateTime(LocalDateTime.now());

        return this.save(professionalManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfessional(Long id, String name, String code, Long departmentId) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(ProfessionalManager::getId, id)
                .eq(ProfessionalManager::getIsDelete, false);
        ProfessionalManager professionalManager = this.getOne(queryWrapperById);
        if (Objects.isNull(professionalManager)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_NOT_EXIST);
        }

        LambdaQueryWrapper<ProfessionalManager> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(ProfessionalManager::getName, name)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            names.removeIf(item -> item.getId().equals(id));
            if (!names.isEmpty()) {
                throw new ServiceException(ConsumerException.PROFESSIONAL_NAME_REPEAT);
            }
        }

        LambdaQueryWrapper<ProfessionalManager> queryWrapperCode = new LambdaQueryWrapper<>();
        queryWrapperCode.eq(ProfessionalManager::getCode, code)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> codes = this.list(queryWrapperCode);
        if (CollectionUtils.isNotEmpty(codes)) {
            codes.removeIf(item -> item.getId().equals(id));
            if (!codes.isEmpty()) {
                throw new ServiceException(ConsumerException.PROFESSIONAL_CODE_REPEAT);
            }
        }

        LambdaQueryWrapper<DepartmentManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(DepartmentManager::getId, departmentId)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentInfo = departmentManagerService.getOne(queryWrapperId);
        if (Objects.isNull(departmentInfo)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NOT_EXIST);
        }

        professionalManager.setName(name);
        professionalManager.setCode(code);
        professionalManager.setDepartmentId(departmentId);
        professionalManager.setDepartmentName(departmentInfo.getName());
        professionalManager.setUpdateTime(LocalDateTime.now());

        if (this.updateById(professionalManager)) {
            return classManagerService.updateProfessional(id, professionalManager.getName());
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProfessional(Long id) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProfessionalManager::getId, id)
                .eq(ProfessionalManager::getIsDelete, false);
        ProfessionalManager professionalManager = this.getOne(queryWrapper);
        if (Objects.isNull(professionalManager)) {
            throw new ServiceException(ConsumerException.PROFESSIONAL_NOT_EXIST);
        }

        professionalManager.setIsDelete(true);
        professionalManager.setUpdateTime(LocalDateTime.now());

        if (this.updateById(professionalManager)) {
            return classManagerService.deleteClassByProfessionalId(id)
                    && loginService.delUserByProfessionalId(id);
        }
        return false;
    }

    //更改院系信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(Long id, String name) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProfessionalManager::getDepartmentId, id)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ProfessionalManager professionalManager : list) {
                professionalManager.setDepartmentId(id);
                professionalManager.setDepartmentName(name);
                professionalManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    //根据院校id删除专业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProfessionalByDepartmentId(Long id) {
        LambdaQueryWrapper<ProfessionalManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProfessionalManager::getDepartmentId, id)
                .eq(ProfessionalManager::getIsDelete, false);
        List<ProfessionalManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ProfessionalManager professionalManager : list) {
                professionalManager.setIsDelete(true);
                professionalManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }
}
