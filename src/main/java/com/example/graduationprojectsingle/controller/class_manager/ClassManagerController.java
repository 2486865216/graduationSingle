package com.example.graduationprojectsingle.controller.class_manager;


import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.class_manager.ClassManager;
import com.example.graduationprojectsingle.entity.class_manager.req.AddClassManagerRequest;
import com.example.graduationprojectsingle.entity.class_manager.req.UpdateClassManagerRequest;
import com.example.graduationprojectsingle.entity.class_manager.resp.ClassManagerResponse;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.class_manager.ClassManagerService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/classManager")
@Slf4j
public class ClassManagerController {

    @Autowired
    private ClassManagerService classManagerService;

    // 查询所有班级
    @GetMapping("/list")
    public CommonResult<List<ClassManagerResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long professionalId
    ) {
        return ResultUtil.success(this.packResponse(classManagerService.getList(search, name, code, departmentId, professionalId)));
    }

    // 新增班级
    @PostMapping("/addClassManager")
    public CommonResult<String> addClassManager(@RequestBody AddClassManagerRequest request) {
        if (request == null || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getCode()) || request.getProfessionalId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = classManagerService.addClassManager(request.getName(), request.getCode(), request.getProfessionalId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 修改课程
    @PutMapping("/updateClassManager")
    public CommonResult<String> updateClassManager(@RequestBody UpdateClassManagerRequest request) {
        if (request == null || request.getId() == null || StringUtils.isEmpty(request.getName())
                || StringUtils.isEmpty(request.getCode()) || request.getProfessionalId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = classManagerService.updateClassManager(request.getId(), request.getName(), request.getCode(), request.getProfessionalId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 删除课程
    @DeleteMapping("/deleteClassManager/{id}")
    public CommonResult<String> deleteClassManager(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = classManagerService.deleteClassManager(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 封装返回的数据
    private List<ClassManagerResponse> packResponse(List<ClassManager> list) {
        List<ClassManagerResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ClassManager classManager : list) {
                ClassManagerResponse classManagerResponse = new ClassManagerResponse();
                classManagerResponse.setId(classManager.getId());
                classManagerResponse.setName(classManager.getName());
                classManagerResponse.setCode(classManager.getCode());
                classManagerResponse.setDepartmentId(classManager.getDepartmentId());
                classManagerResponse.setDepartmentName(classManager.getDepartmentName());
                classManagerResponse.setProfessionalId(classManager.getProfessionalId());
                classManagerResponse.setProfessionalName(classManager.getProfessionalName());
                classManagerResponse.setCreateTime(classManager.getCreateTime());
                classManagerResponse.setUpdateTime(classManager.getUpdateTime());

                responses.add(classManagerResponse);
            }
        }
        return responses;
    }
}
