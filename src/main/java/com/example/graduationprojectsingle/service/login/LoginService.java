package com.example.graduationprojectsingle.service.login;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduationprojectsingle.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoginService extends IService<User> {

    //通过用户名获取用户信息
    User getUserByUsername(String username);

    User getUserById(Long id);

    List<User> getList(String search, String username, String nickname, Long departmentId, Long professionalId, Long classId);

    List<User> getTeacherList(String search, String username, String nickname, Long departmentId);

    boolean addStudent(String username, String nickname, Long classId);

    boolean updateStudent(Long id, String username, String nickname, Long classId);

    boolean deleteStudent(Long id);

    boolean addTeach(String username, String nickname, Long departmentId);

    boolean updateTeach(Long id, String username, String nickname, Long departmentId);

    boolean deleteTeach(Long id);

    boolean delUserByDepartmentId(Long departmentId);

    boolean delUserByProfessionalId(Long professionalId);

    boolean delUserByClassId(Long classId);

    List<User> getUserByCurriculumId(Long id, String search);
}
