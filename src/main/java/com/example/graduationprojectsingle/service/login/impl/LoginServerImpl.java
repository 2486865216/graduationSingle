package com.example.graduationprojectsingle.service.login.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.controller.curriculum_user.CurriculumUserController;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;
import com.example.graduationprojectsingle.entity.curriculum_user.CurriculumUser;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.user.User;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.login.LoginMapper;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
import com.example.graduationprojectsingle.service.curriculum_manager.CurriculumManagerService;
import com.example.graduationprojectsingle.service.curriculum_user.CurriculumUserService;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.loginUtils.PasswordUtil;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoginServerImpl extends ServiceImpl<LoginMapper, User> implements LoginService {

    @Autowired
    private ClassManagerService classManagerService;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private DepartmentManagerService departmentManagerService;

    @Autowired
    @Lazy
    private CurriculumManagerService curriculumManagerService;

    @Autowired
    @Lazy
    private CurriculumUserService curriculumUserService;

    @Autowired
    @Lazy
    private CurriculumUserController curriculumUserController;

    //通过用户名获取用户信息
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                .eq(User::getIsDelete, false);
        return this.getOne(queryWrapper);
    }

    @Override
    public User getUserById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id)
                .eq(User::getIsDelete, false);
        User one = this.getOne(queryWrapper);
        if (one == null) {
            throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        }
        return one;
    }

    public List<User> getList(String search, String username, String nickname, Long departmentId, Long professionalId, Long classId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, 2)
                .eq(User::getIsDelete, false);
        if (StringUtils.isNotEmpty(search)) {
            LambdaQueryWrapper<ClassManager> classNames = new LambdaQueryWrapper<>();
            classNames.like(ClassManager::getName, search)
                    .and(qw -> {
                        qw.or().like(ClassManager::getProfessionalName, search)
                                .or().like(ClassManager::getDepartmentName, search);
                    })
                            .eq(ClassManager::getIsDelete, false);
            List<ClassManager> list = classManagerService.list(classNames);
            queryWrapper.and(qw -> {
                qw.or().like(User::getNickname, search)
                        .or().like(User::getUsername, search);
                if (CollectionUtils.isNotEmpty(list)) {
                    qw.or().in(User::getClassId, list.stream().map(ClassManager::getId).collect(Collectors.toList()));
                }
            });
        }
        if (StringUtils.isNotEmpty(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.isNotEmpty(nickname)) {
            queryWrapper.like(User::getNickname, nickname);
        }
        if (Objects.nonNull(departmentId)) {
            queryWrapper.eq(User::getDepartmentId, departmentId);
        }
        if (Objects.nonNull(professionalId)) {
            queryWrapper.eq(User::getProfessionalId, professionalId);
        }
        if (Objects.nonNull(classId)) {
            queryWrapper.eq(User::getClassId, classId);
        }
        return this.list(queryWrapper);
    }

    public List<User> getTeacherList(String search, String username, String nickname, Long departmentId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, 1)
                .eq(User::getIsDelete, false);
        if (StringUtils.isNotEmpty(search)) {
            LambdaQueryWrapper<ClassManager> classNames = new LambdaQueryWrapper<>();
            classNames.like(ClassManager::getName, search)
                    .and(qw -> {
                        qw.or().like(ClassManager::getProfessionalName, search)
                                .or().like(ClassManager::getDepartmentName, search);
                    })
                    .eq(ClassManager::getIsDelete, false);
            List<ClassManager> list = classManagerService.list(classNames);
            queryWrapper.and(qw -> {
                qw.or().like(User::getNickname, search)
                        .or().like(User::getUsername, search);
                if (CollectionUtils.isNotEmpty(list)) {
                    qw.or().in(User::getClassId, list.stream().map(ClassManager::getId).collect(Collectors.toList()));
                }
            });
        }

        if (StringUtils.isNotEmpty(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.isNotEmpty(nickname)) {
            queryWrapper.like(User::getNickname, nickname);
        }
        if (Objects.nonNull(departmentId)) {
            queryWrapper.eq(User::getDepartmentId, departmentId);
        }
        return this.list(queryWrapper);
    }

    @Override
    public boolean addStudent(String username, String nickname, Long classId) {
        LambdaQueryWrapper<User> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(User::getUsername, username)
                .eq(User::getIsDelete, false);
        List<User> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            throw new ServiceException(ConsumerException.USERNAME_HAS_EXIST);
        }

        LambdaQueryWrapper<ClassManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(ClassManager::getId, classId)
                .eq(ClassManager::getIsDelete, false);
        ClassManager classManager = classManagerService.getOne(queryWrapperId);
        if (Objects.isNull(classManager)) {
            throw new ServiceException(ConsumerException.CLASS_NOT_EXIST);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.generate("123456"));
        user.setNickname(nickname);
        user.setRole(2);
        user.setClassId(classId);
        user.setProfessionalId(classManager.getProfessionalId());
        user.setDepartmentId(classManager.getDepartmentId());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        return this.save(user);
    }

    @Override
    public boolean updateStudent(Long id, String username, String nickname, Long classId) {
        LambdaQueryWrapper<User> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(User::getId, id)
                .eq(User::getRole, 2)
                .eq(User::getIsDelete, false);
        User user = this.getOne(queryWrapperById);
        if (Objects.isNull(user)) {
            throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        }

        LambdaQueryWrapper<User> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(User::getNickname, username)
                .eq(User::getRole, 2)
                .eq(User::getIsDelete, false);
        List<User> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            names.removeIf(item -> item.getId().equals(id));
            if (!names.isEmpty()) {
                throw new ServiceException(ConsumerException.USERNAME_HAS_EXIST);
            }
        }

        LambdaQueryWrapper<ClassManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(ClassManager::getId, classId)
                .eq(ClassManager::getIsDelete, false);
        ClassManager classManager = classManagerService.getOne(queryWrapperId);
        if (Objects.isNull(classManager)) {
            throw new ServiceException(ConsumerException.CLASS_NOT_EXIST);
        }

        user.setUsername(username);
        user.setNickname(nickname);
        user.setClassId(classManager.getId());
        user.setDepartmentId(classManager.getDepartmentId());
        user.setProfessionalId(classManager.getProfessionalId());
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    public boolean deleteStudent(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id)
                .eq(User::getIsDelete, false);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        }
        user.setIsDelete(true);
        user.setUpdateTime(LocalDateTime.now());

        if (this.updateById(user)) {
            curriculumUserService.deleteCurriculumUserByUserId(id);
        }

        return false;
    }

    @Override
    public boolean addTeach(String username, String nickname, Long departmentId) {
        LambdaQueryWrapper<User> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(User::getUsername, username)
                .eq(User::getIsDelete, false);
        List<User> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            throw new ServiceException(ConsumerException.USERNAME_HAS_EXIST);
        }

        LambdaQueryWrapper<DepartmentManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(DepartmentManager::getId, departmentId)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentManager = departmentManagerService.getOne(queryWrapperId);
        if (Objects.isNull(departmentManager)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NOT_EXIST);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.generate("123456"));
        user.setNickname(nickname);
        user.setRole(1);
        user.setDepartmentId(departmentManager.getId());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        return this.save(user);
    }

    @Override
    public boolean updateTeach(Long id, String username, String nickname, Long departmentId) {
        LambdaQueryWrapper<User> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(User::getId, id)
                .eq(User::getRole, 1)
                .eq(User::getIsDelete, false);
        User user = this.getOne(queryWrapperById);
        if (Objects.isNull(user)) {
            throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        }

        LambdaQueryWrapper<User> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(User::getNickname, username)
                .eq(User::getRole, 1)
                .eq(User::getIsDelete, false);
        List<User> names = this.list(queryWrapperName);
        if (CollectionUtils.isNotEmpty(names)) {
            names.removeIf(item -> item.getId().equals(id));
            if (!names.isEmpty()) {
                throw new ServiceException(ConsumerException.USERNAME_HAS_EXIST);
            }
        }

        LambdaQueryWrapper<DepartmentManager> queryWrapperId = new LambdaQueryWrapper<>();
        queryWrapperId.eq(DepartmentManager::getId, departmentId)
                .eq(DepartmentManager::getIsDelete, false);
        DepartmentManager departmentManager = departmentManagerService.getOne(queryWrapperId);
        if (Objects.isNull(departmentManager)) {
            throw new ServiceException(ConsumerException.DEPARTMENT_NOT_EXIST);
        }

        user.setUsername(username);
        user.setNickname(nickname);
        user.setDepartmentId(departmentManager.getId());
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user) && curriculumManagerService.updateTeacher(id, nickname);
    }

    @Override
    public boolean deleteTeach(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id)
                .eq(User::getIsDelete, false);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        }
        user.setIsDelete(true);
        user.setUpdateTime(LocalDateTime.now());

        return this.updateById(user);
    }

    @Override
    public boolean delUserByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDepartmentId, departmentId)
                .ne(User::getRole, 0)
                .eq(User::getIsDelete, false);
        List<User> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (User user : list) {
                user.setIsDelete(true);
                user.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public boolean delUserByProfessionalId(Long professionalId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getProfessionalId, professionalId)
                .ne(User::getRole, 0)
                .eq(User::getIsDelete, false);
        List<User> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (User user : list) {
                user.setIsDelete(true);
                user.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public boolean delUserByClassId(Long classId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getClassId, classId)
                .ne(User::getRole, 0)
                .eq(User::getIsDelete, false);
        List<User> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (User user : list) {
                user.setIsDelete(true);
                user.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public List<User> getUserByCurriculumId(Long id, String search) {
        LambdaQueryWrapper<CurriculumUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumUser::getCurriculumId, id);
        List<CurriculumUser> list = curriculumUserService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(User::getId, list.stream().map(CurriculumUser::getUserId).collect(Collectors.toList()))
                    .eq(User::getIsDelete, false);
            if (StringUtils.isNotEmpty(search)) {
                LambdaQueryWrapper<ClassManager> classNames = new LambdaQueryWrapper<>();
                classNames.like(ClassManager::getName, search)
                        .and(qw -> {
                            qw.or().like(ClassManager::getProfessionalName, search)
                                    .or().like(ClassManager::getDepartmentName, search);
                        })
                        .eq(ClassManager::getIsDelete, false);
                List<ClassManager> list1 = classManagerService.list(classNames);
                queryWrapper1.and(qw -> {
                    qw.or().like(User::getNickname, search)
                            .or().like(User::getUsername, search);
                    if (CollectionUtils.isNotEmpty(list1)) {
                        qw.or().in(User::getClassId, list1.stream().map(ClassManager::getId).collect(Collectors.toList()));
                    }
                });
            }
            return this.list(queryWrapper1);
        }
        return new ArrayList<>();
    }
}
