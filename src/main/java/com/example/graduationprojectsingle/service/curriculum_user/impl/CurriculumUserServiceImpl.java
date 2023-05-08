package com.example.graduationprojectsingle.service.curriculum_user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.curriculum_manager.CurriculumManager;
import com.example.graduationprojectsingle.entity.curriculum_user.CurriculumUser;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.curriculum_user.CurriculumUserMapper;
import com.example.graduationprojectsingle.service.curriculum_manager.CurriculumManagerService;
import com.example.graduationprojectsingle.service.curriculum_user.CurriculumUserService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurriculumUserServiceImpl extends ServiceImpl<CurriculumUserMapper, CurriculumUser> implements CurriculumUserService {

    @Autowired
    private CurriculumManagerService curriculumManagerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCurriculumUser(Long userId, Long curriculumId) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getId, curriculumId)
                .eq(CurriculumManager::getIsDelete, false);
        CurriculumManager one = curriculumManagerService.getOne(queryWrapper);
        if (one == null) {
            throw new ServiceException(ConsumerException.CURRICULUM_NOT_EXIST);
        }
        Integer curCount = one.getCurCount();
        if (curCount < one.getMaxCount()) {
            one.setCurCount(curCount + 1);
            one.setUpdateTime(LocalDateTime.now());
        }
        if (curriculumManagerService.updateById(one)) {
            CurriculumUser curriculumUser = new CurriculumUser();
            curriculumUser.setUserId(userId);
            curriculumUser.setCurriculumId(curriculumId);
            return this.save(curriculumUser);
        }
        return false;
    }

    @Override
    public boolean deleteCurriculumUserByUserId(Long userId) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumUser::getUserId, userId);
        List<CurriculumUser> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurriculumUser curriculumUser : list) {
                LambdaQueryWrapper<CurriculumManager> queryWrapperCurriculum = new LambdaQueryWrapper<>();
                queryWrapperCurriculum.eq(CurriculumManager::getId, curriculumUser.getCurriculumId())
                        .eq(CurriculumManager::getIsDelete, false);
                CurriculumManager one = curriculumManagerService.getOne(queryWrapperCurriculum);
                if (one == null) {
                    throw new ServiceException(ConsumerException.CURRICULUM_NOT_EXIST);
                }
                Integer curCount = one.getCurCount();
                if (curCount > 0) {
                    one.setCurCount(curCount - 1);
                    one.setUpdateTime(LocalDateTime.now());
                }
            }
        }
        return this.remove(queryWrapper);
    }

    @Override
    public boolean deleteCurriculumUserByCurriculumId(Long curriculumId) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumUser::getCurriculumId, curriculumId);
        return this.remove(queryWrapper);
    }

    @Override
    public List<CurriculumUser> getList(Long userId, Long curriculumId) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq(CurriculumUser::getUserId, userId);
        }
        if (curriculumId != null) {
            queryWrapper.eq(CurriculumUser::getCurriculumId, curriculumId);
        }
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCurriculumUserByUserIdAndCurriculumId(Long userId, Long curriculumId) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumUser::getUserId, userId)
                .eq(CurriculumUser::getCurriculumId, curriculumId);
        if (this.remove(queryWrapper)) {
            LambdaQueryWrapper<CurriculumManager> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(CurriculumManager::getId, curriculumId)
                    .eq(CurriculumManager::getIsDelete, false);
            CurriculumManager one = curriculumManagerService.getOne(queryWrapper1);
            if (one == null) {
                throw new ServiceException(ConsumerException.CURRICULUM_NOT_EXIST);
            }
            Integer curCount = one.getCurCount();
            if (curCount > 0) {
                one.setCurCount(curCount - 1);
            }
            one.setUpdateTime(LocalDateTime.now());
            return curriculumManagerService.updateById(one);
        }
        return false;
    }

    @Override
    public List<Long> getByUserId(Long userId) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumUser::getUserId, userId);
        List<CurriculumUser> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(CurriculumUser::getCurriculumId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
