package com.example.graduationprojectsingle.controller.login;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.student_manager.resp.StudentResponse;
import com.example.graduationprojectsingle.entity.teacher_manager.resp.TeachResponse;
import com.example.graduationprojectsingle.entity.user.TokenUser;
import com.example.graduationprojectsingle.entity.user.User;
import com.example.graduationprojectsingle.entity.user.request.SignInRequest;
import com.example.graduationprojectsingle.entity.user.request.UpdatePasswordRequest;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.service.login.LoginService;
import com.example.graduationprojectsingle.utils.loginUtils.PasswordUtil;
import com.example.graduationprojectsingle.utils.loginUtils.TokenUtil;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private LoginService loginService;

    @Autowired
    private DepartmentManagerService departmentManagerService;

    @Autowired
    private ClassManagerService classManagerService;

    //登录
    @PostMapping("/passwordLogin")
    public CommonResult<String> passwordLogin(@RequestBody User user) {
        if (Objects.isNull(user) || StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        User userDataBase = loginService.getUserByUsername(user.getUsername());

        if (Objects.isNull(userDataBase)) {
            throw new ServiceException(ConsumerException.USERNAME_ERROR);
        }

        boolean verify = passwordUtil.verify(user.getPassword(), userDataBase.getPassword());

        if (verify) {
            return ResultUtil.success(tokenUtil.createToken(userDataBase));
        }

        throw new ServiceException(ConsumerException.PASSWORD_ERROR);
    }

    @GetMapping("/getInfo")
    public CommonResult<Object> getInfo(HttpServletRequest request) {
        try {
            TokenUser user = tokenUtil.getUser(request);
            User loginServiceById = loginService.getById(user.getId());
            if (loginServiceById == null) throw new ServiceException(ConsumerException.USERNAME_ERROR);
            Integer role = loginServiceById.getRole();
            if (role == 1) {
                LambdaQueryWrapper<DepartmentManager> queryWrapperDepartment = new LambdaQueryWrapper<>();
                queryWrapperDepartment.eq(DepartmentManager::getId, loginServiceById.getDepartmentId())
                        .eq(DepartmentManager::getIsDelete, false);
                DepartmentManager one = departmentManagerService.getOne(queryWrapperDepartment);
                if (one != null) {
                    TeachResponse response = new TeachResponse();
                    response.setId(loginServiceById.getId());
                    response.setUsername(loginServiceById.getUsername());
                    response.setNickname(loginServiceById.getNickname());
                    response.setRole(loginServiceById.getRole());
                    response.setDepartmentId(loginServiceById.getDepartmentId());
                    response.setDepartmentName(one.getName());
                    return ResultUtil.success(response);
                }
            } else if (role == 2) {
                LambdaQueryWrapper<ClassManager> queryWrapperDepartment = new LambdaQueryWrapper<>();
                queryWrapperDepartment.eq(ClassManager::getId, loginServiceById.getClassId())
                        .eq(ClassManager::getIsDelete, false);
                ClassManager one = classManagerService.getOne(queryWrapperDepartment);
                if (one != null) {
                    StudentResponse response = new StudentResponse();
                    response.setId(loginServiceById.getId());
                    response.setUsername(loginServiceById.getUsername());
                    response.setNickname(loginServiceById.getNickname());
                    response.setRole(loginServiceById.getRole());
                    response.setClassId(one.getId());
                    response.setClassName(one.getName());
                    response.setProfessionalId(one.getProfessionalId());
                    response.setProfessionalName(one.getProfessionalName());
                    response.setDepartmentId(one.getDepartmentId());
                    response.setDepartmentName(one.getDepartmentName());

                    return ResultUtil.success(response);
                }
            }
            loginServiceById.setPassword(null);
            return ResultUtil.success(loginServiceById);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(ConsumerException.AUTHORIZATION_USER_ERROR);
        }
    }

    @PutMapping("/updatePassword")
    public CommonResult<String> updatePassword(HttpServletRequest request, @RequestBody UpdatePasswordRequest passwordRequest) {
        if (passwordRequest == null || StringUtils.isEmpty(passwordRequest.getOldPassword()) || StringUtils.isEmpty(passwordRequest.getNewPassword())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        TokenUser user = tokenUtil.getUser(request);
        if (user == null) throw new ServiceException(ConsumerException.AUTHORIZATION_USER_ERROR);
        User byId = loginService.getById(user.getId());
        if (byId == null) throw new ServiceException(ConsumerException.USER_NOT_EXIST);
        if (!passwordUtil.verify(passwordRequest.getOldPassword(), byId.getPassword())) {
            return ResultUtil.error("旧密码不正确!");
        }
        byId.setPassword(passwordUtil.generate(passwordRequest.getNewPassword()));
        byId.setUpdateTime(LocalDateTime.now());

        if (loginService.updateById(byId)) {
            return ResultUtil.success();
        } else {
            return ResultUtil.error();
        }
    }

    @PostMapping("/signIn")
    public CommonResult<String> signIn(@RequestBody SignInRequest request) {
        if (Objects.isNull(request) || StringUtils.isEmpty(request.getUsername()) || StringUtils.isEmpty(request.getPassword())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername())
                .eq(User::getIsDelete, false);
        User one = loginService.getOne(queryWrapper);
        if (Objects.nonNull(one)) {
            return ResultUtil.error("用户名重复!");
        }

        //加密密码
        String password = passwordUtil.generate(request.getPassword());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(password);

        if (loginService.save(user)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }
}
