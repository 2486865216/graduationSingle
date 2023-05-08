package com.example.graduationprojectsingle.service.class_room.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduationprojectsingle.entity.class_room_manager.ClassRoom;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.mapper.class_room_manager.ClassRoomManagerMapper;
import com.example.graduationprojectsingle.service.class_room.ClassRoomManagerService;
import org.springframework.stereotype.Service;

@Service
public class ClassRoomManagerServiceImpl extends ServiceImpl<ClassRoomManagerMapper, ClassRoom> implements ClassRoomManagerService {
    @Override
    public ClassRoom getClassRoomById(Long id) {
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getId, id)
                .eq(ClassRoom::getIsDelete, false);
        ClassRoom one = this.getOne(queryWrapper);
        if (one == null) {
            throw new ServiceException(ConsumerException.CLASS_ROOM_NOT_EXIST);
        }
        return one;
    }
}
