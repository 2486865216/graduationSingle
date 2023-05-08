package com.example.graduationprojectsingle.controller.class_room_manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.class_room_manager.ClassRoom;
import com.example.graduationprojectsingle.entity.class_room_manager.req.AddClassRoomRequest;
import com.example.graduationprojectsingle.entity.class_room_manager.req.UpdateClassRoomRequest;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.class_room.ClassRoomManagerService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/class/room")
public class ClassRoomManagerController {

    @Autowired
    private ClassRoomManagerService classRoomManagerService;

    @GetMapping("/list")
    public CommonResult<List<ClassRoom>> getList(String name) {
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.like(ClassRoom::getName, name);
        }
        queryWrapper.eq(ClassRoom::getIsDelete, false);
        List<ClassRoom> list = classRoomManagerService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Collections.sort(list, Comparator.comparing(ClassRoom::getName));
        }
        return ResultUtil.success(list);
    }

    @PostMapping("/addClassRoom")
    public CommonResult<String> addClassRoom(@RequestBody AddClassRoomRequest request) {
        if (request == null || StringUtils.isEmpty(request.getName())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        String name = request.getName();
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getName, name)
                .eq(ClassRoom::getIsDelete, false);
        List<ClassRoom> list = classRoomManagerService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return ResultUtil.error("教室名称重复!");
        }

        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(name);
        if (classRoomManagerService.save(classRoom)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @PostMapping("/updateClassRoom")
    public CommonResult<String> updateClassRoom(@RequestBody UpdateClassRoomRequest request) {
        if (request == null || StringUtils.isEmpty(request.getName()) || request.getId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        Long id = request.getId();
        String name = request.getName();
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getName, name)
                .eq(ClassRoom::getIsDelete, false);
        List<ClassRoom> list = classRoomManagerService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list.removeIf(item -> id.equals(item.getId()));
            if (CollectionUtils.isNotEmpty(list)) {
                return ResultUtil.error("教室名称重复!");
            }
        }

        LambdaQueryWrapper<ClassRoom> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ClassRoom::getId, id)
                .eq(ClassRoom::getIsDelete, false);
        ClassRoom one = classRoomManagerService.getOne(queryWrapper1);
        if (one == null) {
            return ResultUtil.error("教室不存在!");
        }
        one.setName(name);
        if (classRoomManagerService.updateById(one)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @PostMapping("/deleteClassRoom/{id}")
    public CommonResult<String> deleteClassRoom(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getId, id)
                .eq(ClassRoom::getIsDelete, false);
        ClassRoom one = classRoomManagerService.getOne(queryWrapper);
        if (one == null) {
            return ResultUtil.error("教室不存在!");
        }
        one.setIsDelete(true);
        if (classRoomManagerService.updateById(one)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }
}
