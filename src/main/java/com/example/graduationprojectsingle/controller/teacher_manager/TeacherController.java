package com.example.graduationprojectsingle.controller.teacher_manager;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.teacher_manager.req.AddTeachRequest;
import com.example.graduationprojectsingle.entity.teacher_manager.req.UpdateTeachRequest;
import com.example.graduationprojectsingle.entity.teacher_manager.resp.TeachResponse;
import com.example.graduationprojectsingle.entity.user.User;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private DepartmentManagerService departmentManagerService;

    @GetMapping("/list")
    public CommonResult<List<TeachResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long departmentId
    ) {
        List<User> list = loginService.getTeacherList(search, username, nickname, departmentId);
        return ResultUtil.success(this.packStudentResponse(list));
    }

    @PostMapping("/addTeach")
    public CommonResult<String> addTeach(@RequestBody AddTeachRequest request) {
        if (request == null || request.getUsername() == null || request.getNickname() == null || request.getDepartmentId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.addTeach(request.getUsername(), request.getNickname(), request.getDepartmentId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @PutMapping("/updateTeach")
    public CommonResult<String> updateTeach(@RequestBody UpdateTeachRequest request) {
        if (request == null || request.getId() == null || request.getUsername() == null || request.getNickname() == null || request.getDepartmentId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.updateTeach(request.getId(), request.getUsername(), request.getNickname(), request.getDepartmentId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @DeleteMapping("/deleteTeach/{id}")
    public CommonResult<String> deleteTeach(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = loginService.deleteTeach(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    private List<TeachResponse> packStudentResponse(List<User> list) {
        List<TeachResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            List<DepartmentManager> list1 = departmentManagerService.getList(null, null);
            Map<Long, DepartmentManager> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list1)) {
                for (DepartmentManager departmentManager : list1) {
                    map.put(departmentManager.getId(), departmentManager);
                }
            }
            for (User user : list) {
                TeachResponse response = new TeachResponse();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setNickname(user.getNickname());
                response.setRole(user.getRole());
                DepartmentManager departmentManager = map.get(user.getDepartmentId());
                if (departmentManager != null) {
                    response.setDepartmentId(departmentManager.getId());
                    response.setDepartmentName(departmentManager.getName());
                }
                response.setCreateTime(user.getCreateTime());
                response.setUpdateTime(user.getUpdateTime());
                response.setAvatarUrl(user.getAvatarUrl());

                responses.add(response);
            }
            Collections.sort(responses, Comparator.comparing(TeachResponse::getUsername));
        }
        return responses;
    }
}
