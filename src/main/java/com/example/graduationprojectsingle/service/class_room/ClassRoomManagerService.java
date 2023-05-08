package com.example.graduationprojectsingle.service.class_room;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.class_room_manager.ClassRoom;

public interface ClassRoomManagerService extends IService<ClassRoom> {
    ClassRoom getClassRoomById(Long id);
}
