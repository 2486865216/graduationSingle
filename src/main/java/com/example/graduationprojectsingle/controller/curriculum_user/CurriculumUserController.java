package com.example.graduationprojectsingle.controller.curriculum_user;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.curriculum_user.CurriculumUser;
import com.example.graduationprojectsingle.entity.curriculum_user.req.AddCurriculumUserRequest;
import com.example.graduationprojectsingle.entity.curriculum_user.req.DeleteCurriculumUserRequest;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.curriculum_user.CurriculumUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculum/user")
public class CurriculumUserController {

    @Autowired
    private CurriculumUserService curriculumUserService;

    @GetMapping("/list")
    public CommonResult<List<CurriculumUser>> list(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long curriculumId) {
        return ResultUtil.success(curriculumUserService.getList(userId, curriculumId));
    }

    @PostMapping("/addCurriculumUser")
    public CommonResult<String> addCurriculumUser(@RequestBody AddCurriculumUserRequest request) {
        if (request == null || request.getUserId() == null || request.getCurriculumId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = curriculumUserService.addCurriculumUser(request.getUserId(), request.getCurriculumId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @PostMapping("/deleteCurriculumUser")
    public CommonResult<String> deleteCurriculumUser(@RequestBody DeleteCurriculumUserRequest request) {
        if (request == null || request.getUserId() == null || request.getCurriculumId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = curriculumUserService.deleteCurriculumUserByUserIdAndCurriculumId(request.getUserId(), request.getCurriculumId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }
}
