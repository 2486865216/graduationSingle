package com.example.graduationprojectsingle.controller.student_manager;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.controller.curriculum_user.CurriculumUserController;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;
import com.example.graduationprojectsingle.entity.student_manager.req.AddStudentRequest;
import com.example.graduationprojectsingle.entity.student_manager.req.UpdateStudentRequest;
import com.example.graduationprojectsingle.entity.student_manager.resp.StudentResponse;
import com.example.graduationprojectsingle.entity.user.User;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ClassManagerService classManagerService;

    @GetMapping("/list")
    public CommonResult<List<StudentResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) Long classId
    ) {
        List<User> list = loginService.getList(search, username, nickname, departmentId, professionalId, classId);
        return ResultUtil.success(this.packStudentResponse(list));
    }

    @GetMapping("/getUserByCurriculumId")
    public CommonResult<List<StudentResponse>> getUserByCurriculumId(@RequestParam Long id, @RequestParam(required = false) String search) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        List<User> users = loginService.getUserByCurriculumId(id, search);
        return ResultUtil.success(this.packStudentResponse(users));
    }

    @PostMapping("/addStudent")
    public CommonResult<String> addStudent(@RequestBody AddStudentRequest request) {
        if (request == null || request.getUsername() == null || request.getNickname() == null || request.getClassId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.addStudent(request.getUsername(), request.getNickname(), request.getClassId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @PutMapping("/updateStudent")
    public CommonResult<String> updateStudent(@RequestBody UpdateStudentRequest request) {
        if (request == null || request.getId() == null || request.getUsername() == null || request.getNickname() == null || request.getClassId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.updateStudent(request.getId(), request.getUsername(), request.getNickname(), request.getClassId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @DeleteMapping("/deleteStudent/{id}")
    public CommonResult<String> deleteStudent(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.deleteStudent(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    private List<StudentResponse> packStudentResponse(List<User> list) {
        List<StudentResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            List<ClassManager> list1 = classManagerService.getList(null, null, null, null, null);
            Map<Long, ClassManager> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list1)) {
                for (ClassManager classManager : list1) {
                    map.put(classManager.getId(), classManager);
                }
            }
            for (User user : list) {
                StudentResponse response = new StudentResponse();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setNickname(user.getNickname());
                response.setRole(user.getRole());
                response.setClassId(user.getClassId());
                ClassManager classManager = map.get(user.getClassId());
                if (classManager != null) {
                    response.setClassName(classManager.getName());
                    response.setProfessionalId(classManager.getProfessionalId());
                    response.setProfessionalName(classManager.getProfessionalName());
                    response.setDepartmentId(classManager.getDepartmentId());
                    response.setDepartmentName(classManager.getDepartmentName());
                }
                response.setCreateTime(user.getCreateTime());
                response.setUpdateTime(user.getUpdateTime());
                response.setAvatarUrl(user.getAvatarUrl());

                responses.add(response);
            }
            Collections.sort(responses, Comparator.comparing(StudentResponse::getUsername));
        }
        return responses;
    }
}
