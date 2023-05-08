package com.example.graduationprojectsingle.service.curriculum_manager.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.class_room_manager.ClassRoom;
import com.example.graduationprojectsingle.entity.curriculum_manager.CurriculumManager;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.AddCurriculumRequest;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.UpdateCurriculumRequest;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.user.User;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.curriculum_manage.CurriculumManagerMapper;
import com.example.graduationprojectsingle.service.class_room.ClassRoomManagerService;
import com.example.graduationprojectsingle.service.curriculum_manager.CurriculumManagerService;
import com.example.graduationprojectsingle.service.curriculum_user.CurriculumUserService;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CurriculumManagerServiceImpl extends ServiceImpl<CurriculumManagerMapper, CurriculumManager> implements CurriculumManagerService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ClassRoomManagerService classRoomManagerService;

    @Autowired
    private DepartmentManagerService departmentManagerService;

    @Autowired
    @Lazy
    private CurriculumUserService curriculumUserService;

    @Override
    public List<CurriculumManager> getList(String search, String name, String code, Double credit, Integer grade, Long departmentId) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(search)) {
            queryWrapper.and(qw -> {
                qw.or().like(CurriculumManager::getName, search)
                        .or().like(CurriculumManager::getCode, search)
                        .or().like(CurriculumManager::getTeacherName, search)
                        .or().like(CurriculumManager::getClassRoomName, search)
                        .or().like(CurriculumManager::getDepartmentName, search);
            });
        }
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(CurriculumManager::getName, name);
        }
        if (StringUtils.isNotEmpty(code)) {
            queryWrapper.like(CurriculumManager::getCode, code);
        }
        if (credit != null) {
            queryWrapper.eq(CurriculumManager::getCredit, credit);
        }
        if (grade != null) {
            queryWrapper.eq(CurriculumManager::getGrade, grade);
        }
        if (departmentId != null) {
            queryWrapper.eq(CurriculumManager::getDepartmentId, departmentId);
        }
        queryWrapper.eq(CurriculumManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    public List<CurriculumManager> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getTeacherId, teacherId)
                .eq(CurriculumManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    public List<CurriculumManager> getByClassRoomId(Long classRoomId) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getClassRoomId, classRoomId)
                .eq(CurriculumManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCurriculum(AddCurriculumRequest request) {
        String name = request.getName();
        String code = request.getCode();
        Double credit = request.getCredit();
        Integer grade = request.getGrade();
        Long teacherId = request.getTeacherId();
        Long classRoomId = request.getClassRoomId();
        List<Integer> weekTime = request.getWeekTime();
        List<List<Integer>> timeDay = request.getTimeDay();
        Integer maxCount = request.getMaxCount();
        Long departmentId = request.getDepartmentId();

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(code)
                || credit == null || teacherId == null
                || classRoomId == null || weekTime == null || timeDay == null
                || maxCount == null || departmentId == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        User teacher = loginService.getUserById(teacherId);
        ClassRoom classRoom = classRoomManagerService.getClassRoomById(classRoomId);
        DepartmentManager departmentManager = departmentManagerService.getDepartmentById(departmentId);

        if (CollectionUtils.isNotEmpty(this.getByName(name))) {
            throw new ServiceException(ConsumerException.CURRICULUM_NAME_EXIST);
        }

        if (CollectionUtils.isNotEmpty(this.getByCode(code))) {
            throw new ServiceException(ConsumerException.CURRICULUM_CODE_EXIST);
        }

        List<CurriculumManager> byTeacherId = this.getByTeacherId(teacherId);
        int minWeek = weekTime.get(0);
        int maxWeek = weekTime.get(1);
        if (CollectionUtils.isNotEmpty(byTeacherId)) {
            for (CurriculumManager curriculumManager : byTeacherId) {
                List<Integer> weekTime1 = JSONArray.parseArray(curriculumManager.getWeekTime(), Integer.class);
                List<List> lists = JSONArray.parseArray(curriculumManager.getTimeDay(), List.class);
                if (!(weekTime1.get(1) < minWeek || weekTime1.get(0) > maxWeek)) {
                    List<Integer> integerList = timeDay.get(0);
                    List Temp = lists.get(0);
                    if (Objects.equals(integerList.get(0), (Integer) Temp.get(0))) {
                        if (!(integerList.get(1) > (Integer) Temp.get(2) || integerList.get(2) < (Integer) Temp.get(1))) {
                            throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                        }
                    }
                    if (timeDay.size() == 2 && lists.size() == 1) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(0);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 2 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 1 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(0);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    }
                }
            }
        }

        List<CurriculumManager> byClassRoomId = this.getByClassRoomId(classRoomId);
        if (CollectionUtils.isNotEmpty(byClassRoomId)) {
            for (CurriculumManager curriculumManager : byClassRoomId) {
                List<Integer> weekTime1 = JSONArray.parseArray(curriculumManager.getWeekTime(), Integer.class);
                List<List> lists = JSONArray.parseArray(curriculumManager.getTimeDay(), List.class);
                if (!(weekTime1.get(1) < minWeek || weekTime1.get(0) > maxWeek)) {
                    List<Integer> integerList = timeDay.get(0);
                    List listTemp = lists.get(0);
                    if (Objects.equals(integerList.get(0), (Integer) listTemp.get(0))) {
                        if (!(integerList.get(1) > (Integer) listTemp.get(2) || integerList.get(2) < (Integer) listTemp.get(1))) {
                            throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                        }
                    }
                    if (timeDay.size() == 2 && lists.size() == 1) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(0);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 2 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 1 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(0);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    }
                }
            }
        }

        CurriculumManager curriculumManager = new CurriculumManager();
        curriculumManager.setName(name);
        curriculumManager.setCode(code);
        curriculumManager.setCredit(credit);
        curriculumManager.setGrade(grade);
        curriculumManager.setTeacherId(teacher.getId());
        curriculumManager.setTeacherName(teacher.getNickname());
        curriculumManager.setClassRoomId(classRoom.getId());
        curriculumManager.setClassRoomName(classRoom.getName());
        curriculumManager.setWeekTime(JSONObject.toJSONString(weekTime));
        curriculumManager.setTimeDay(JSONObject.toJSONString(timeDay));
        curriculumManager.setMaxCount(maxCount);
        curriculumManager.setDepartmentId(departmentManager.getId());
        curriculumManager.setDepartmentName(departmentManager.getName());
        curriculumManager.setCreateTime(LocalDateTime.now());
        curriculumManager.setUpdateTime(LocalDateTime.now());

        return this.save(curriculumManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCurriculum(UpdateCurriculumRequest request) {
        Long id = request.getId();
        String name = request.getName();
        String code = request.getCode();
        Double credit = request.getCredit();
        Integer grade = request.getGrade();
        Long teacherId = request.getTeacherId();
        Long classRoomId = request.getClassRoomId();
        List<Integer> weekTime = request.getWeekTime();
        List<List<Integer>> timeDay = request.getTimeDay();
        Integer maxCount = request.getMaxCount();
        Long departmentId = request.getDepartmentId();

        if (id == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(code)
                || credit == null || teacherId == null
                || classRoomId == null || weekTime == null || timeDay == null
                || maxCount == null || departmentId == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        User teacher = loginService.getUserById(teacherId);
        ClassRoom classRoom = classRoomManagerService.getClassRoomById(classRoomId);
        DepartmentManager departmentManager = departmentManagerService.getDepartmentById(departmentId);

        List<CurriculumManager> byName = this.getByName(name);
        if (CollectionUtils.isNotEmpty(byName)) {
            byName.removeIf(item -> item.getId().equals(id));
            if (CollectionUtils.isNotEmpty(byName)) {
                throw new ServiceException(ConsumerException.CURRICULUM_NAME_EXIST);
            }
        }

        List<CurriculumManager> byCode = this.getByCode(code);
        if (CollectionUtils.isNotEmpty(byCode)) {
            byCode.removeIf(item -> item.getId().equals(id));
            if (CollectionUtils.isNotEmpty(byCode)) {
                throw new ServiceException(ConsumerException.CURRICULUM_CODE_EXIST);
            }
        }

        List<CurriculumManager> byTeacherId = this.getByTeacherId(teacherId);
        int minWeek = weekTime.get(0);
        int maxWeek = weekTime.get(1);
        if (CollectionUtils.isNotEmpty(byTeacherId)) {
            for (CurriculumManager curriculumManager : byTeacherId) {
                List<Integer> weekTime1 = JSONArray.parseArray(curriculumManager.getWeekTime(), Integer.class);
                List<List> lists = JSONArray.parseArray(curriculumManager.getTimeDay(), List.class);
                if (!(weekTime1.get(1) < minWeek || weekTime1.get(0) > maxWeek)) {
                    List<Integer> integerList = timeDay.get(0);
                    List Temp = lists.get(0);
                    if (Objects.equals(integerList.get(0), (Integer) Temp.get(0))) {
                        if (!(integerList.get(1) > (Integer) Temp.get(2) || integerList.get(2) < (Integer) Temp.get(1))) {
                            throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                        }
                    }
                    if (timeDay.size() == 2 && lists.size() == 1) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(0);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 2 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 1 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(0);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.TEACHER_TIME_ERROR);
                            }
                        }
                    }
                }
            }
        }

        List<CurriculumManager> byClassRoomId = this.getByClassRoomId(classRoomId);
        if (CollectionUtils.isNotEmpty(byClassRoomId)) {
            for (CurriculumManager curriculumManager : byClassRoomId) {
                List<Integer> weekTime1 = JSONArray.parseArray(curriculumManager.getWeekTime(), Integer.class);
                List<List> lists = JSONArray.parseArray(curriculumManager.getTimeDay(), List.class);
                if (!(weekTime1.get(1) < minWeek || weekTime1.get(0) > maxWeek)) {
                    List<Integer> integerList = timeDay.get(0);
                    List listTemp = lists.get(0);
                    if (Objects.equals(integerList.get(0), (Integer) listTemp.get(0))) {
                        if (!(integerList.get(1) > (Integer) listTemp.get(2) || integerList.get(2) < (Integer) listTemp.get(1))) {
                            throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                        }
                    }
                    if (timeDay.size() == 2 && lists.size() == 1) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(0);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 2 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(1);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    } else if (timeDay.size() == 1 && lists.size() == 2) {
                        List<Integer> integers = timeDay.get(0);
                        List list = lists.get(1);
                        if (Objects.equals(integers.get(0), (Integer) list.get(0))) {
                            if (!(integers.get(1) > (Integer) list.get(2) || integers.get(2) < (Integer) list.get(1))) {
                                throw new ServiceException(ConsumerException.CLASS_ROOM_TIME_ERROR);
                            }
                        }
                    }
                }
            }
        }

        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getId, id)
                .eq(CurriculumManager::getIsDelete, false);
        CurriculumManager curriculumManager = this.getOne(queryWrapper);
        curriculumManager.setName(name);
        curriculumManager.setCode(code);
        curriculumManager.setCredit(credit);
        curriculumManager.setGrade(grade);
        curriculumManager.setTeacherId(teacher.getId());
        curriculumManager.setTeacherName(teacher.getNickname());
        curriculumManager.setClassRoomId(classRoom.getId());
        curriculumManager.setClassRoomName(classRoom.getName());
        curriculumManager.setWeekTime(JSONObject.toJSONString(weekTime));
        curriculumManager.setTimeDay(JSONObject.toJSONString(timeDay));
        curriculumManager.setMaxCount(maxCount);
        curriculumManager.setDepartmentId(departmentManager.getId());
        curriculumManager.setDepartmentName(departmentManager.getName());
        curriculumManager.setUpdateTime(LocalDateTime.now());

        return this.updateById(curriculumManager);
    }

    @Override
    public boolean deleteCurriculumManager(Long id) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getId, id)
                .eq(CurriculumManager::getIsDelete, false);
        CurriculumManager curriculumManager = this.getOne(queryWrapper);
        if (Objects.isNull(curriculumManager)) {
            throw new ServiceException(ConsumerException.CURRICULUM_NOT_EXIST);
        }

        curriculumManager.setIsDelete(true);
        curriculumManager.setUpdateTime(LocalDateTime.now());
        curriculumUserService.deleteCurriculumUserByCurriculumId(id);
        return this.updateById(curriculumManager);
    }

    // 更改院系信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(Long id, String name) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getDepartmentId, id)
                .eq(CurriculumManager::getIsDelete, false);
        List<CurriculumManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurriculumManager curriculumManager : list) {
                curriculumManager.setDepartmentId(id);
                curriculumManager.setDepartmentName(name);
                curriculumManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    // 根据院校id删除专业
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCurriculumByDepartmentId(Long id) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getDepartmentId, id)
                .eq(CurriculumManager::getIsDelete, false);
        List<CurriculumManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurriculumManager curriculumManager : list) {
                curriculumManager.setIsDelete(true);
                curriculumManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeacher(Long id, String name) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getDepartmentId, id)
                .eq(CurriculumManager::getIsDelete, false);
        List<CurriculumManager> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurriculumManager curriculumManager : list) {
                curriculumManager.setTeacherId(id);
                curriculumManager.setTeacherName(name);
                curriculumManager.setUpdateTime(LocalDateTime.now());
            }
            return this.updateBatchById(list);
        }
        return true;
    }

    @Override
    public List<CurriculumManager> getByUserId(Long userId) {
        List<Long> curriculumIds = curriculumUserService.getByUserId(userId);
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(curriculumIds)) {
            queryWrapper.in(CurriculumManager::getId, curriculumIds)
                    .eq(CurriculumManager::getIsDelete, false);
            return this.list(queryWrapper);
        }else {
            return new ArrayList<>();
        }
    }

    private List<CurriculumManager> getByName(String name) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getName, name)
                .eq(CurriculumManager::getIsDelete, false);
        return this.list(queryWrapper);
    }

    private List<CurriculumManager> getByCode(String code) {
        LambdaQueryWrapper<CurriculumManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumManager::getCode, code)
                .eq(CurriculumManager::getIsDelete, false);
        return this.list(queryWrapper);
    }
}
