package com.example.graduationprojectsingle.controller.professional_manager;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.professional_manager.ProfessionalManager;
import com.example.graduationprojectsingle.entity.professional_manager.req.AddProfessionalManagerRequest;
import com.example.graduationprojectsingle.entity.professional_manager.req.UpdateProfessionalManagerRequest;
import com.example.graduationprojectsingle.entity.professional_manager.resp.ProfessionalManagerResponse;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.professional_manager.ProfessionalManagerService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/professional")
public class ProfessionalController {
    @Autowired
    private ProfessionalManagerService professionalManagerService;

    // 查询所有专业
    @GetMapping("/list")
    public CommonResult<List<ProfessionalManagerResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long departmentId) {
        return ResultUtil.success(this.packResponse(professionalManagerService.getList(search, name, code, departmentId)));
    }

    // 新增专业
    @PostMapping("/addProfessional")
    public CommonResult<String> addProfessional(@RequestBody AddProfessionalManagerRequest request) {
        if (request == null || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getCode()) || request.getDepartmentId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = professionalManagerService.addProfessional(request.getName(), request.getCode(), request.getDepartmentId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 修改专业
    @PutMapping("/updateProfessional")
    public CommonResult<String> updateProfessional(@RequestBody UpdateProfessionalManagerRequest request) {
        if (request == null || request.getId() == null || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getCode()) || request.getDepartmentId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = professionalManagerService.updateProfessional(request.getId(), request.getName(), request.getCode(), request.getDepartmentId());
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 删除专业
    @DeleteMapping("/deleteProfessional/{id}")
    public CommonResult<String> deleteProfessional(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = professionalManagerService.deleteProfessional(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    // 封装返回的数据
    private List<ProfessionalManagerResponse> packResponse(List<ProfessionalManager> list) {
        List<ProfessionalManagerResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ProfessionalManager professionalManager : list) {
                ProfessionalManagerResponse professionalManagerResponse = new ProfessionalManagerResponse();
                professionalManagerResponse.setId(professionalManager.getId());
                professionalManagerResponse.setName(professionalManager.getName());
                professionalManagerResponse.setCode(professionalManager.getCode());
                professionalManagerResponse.setDepartmentId(professionalManager.getDepartmentId());
                professionalManagerResponse.setDepartmentName(professionalManager.getDepartmentName());
                professionalManagerResponse.setCreateTime(professionalManager.getCreateTime());
                professionalManagerResponse.setUpdateTime(professionalManager.getUpdateTime());

                responses.add(professionalManagerResponse);
            }
        }
        return responses;
    }
}
