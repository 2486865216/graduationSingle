package com.example.graduationprojectsingle.controller.department_manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.department_manager.DepartmentManager;
import com.example.graduationprojectsingle.entity.department_manager.req.AddDepartmentRequest;
import com.example.graduationprojectsingle.entity.department_manager.req.UpdateDepartmentRequest;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.department_manager.DepartmentManagerService;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departmentManager")
public class DepartmentController {

    @Autowired
    private DepartmentManagerService departmentManagerService;

    //查询所有院系
    @GetMapping("/list")
    public CommonResult<List<DepartmentManager>> list(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String code) {
        return ResultUtil.success(departmentManagerService.getList(name, code));
    }

    @GetMapping("/getById")
    public CommonResult<DepartmentManager> getById(@RequestParam Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        LambdaQueryWrapper<DepartmentManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentManager::getId, id)
                        .eq(DepartmentManager::getIsDelete, false);
        return ResultUtil.success(departmentManagerService.getOne(queryWrapper));
    }

    //新增院系
    @PostMapping("/addDepartment")
    public CommonResult<String> addDepartment(@RequestBody AddDepartmentRequest request) {
        if (request == null || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getCode())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = departmentManagerService.addDepartment(request.getName(), request.getCode());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    //修改院系
    @PutMapping("/updateDepartment")
    public CommonResult<String> updateDepartment(@RequestBody UpdateDepartmentRequest request) {
        if (request == null || request.getId() == null || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getCode())) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = departmentManagerService.updateDepartment(request.getId(), request.getName(), request.getCode());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    //删除院系
    @DeleteMapping("/deleteDepartment/{id}")
    public CommonResult<String> deleteDepartment(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = departmentManagerService.deleteDepartment(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }
}
