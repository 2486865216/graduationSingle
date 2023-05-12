package com.example.graduationprojectsingle.controller.curriculum_manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.curriculum_manager.CurriculumManager;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.AddCurriculumRequest;
import com.example.graduationprojectsingle.entity.curriculum_manager.req.UpdateCurriculumRequest;
import com.example.graduationprojectsingle.entity.curriculum_manager.resp.CurriculumResponse;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.curriculum_manager.CurriculumManagerService;
import com.example.graduationprojectsingle.utils.collectionUtils.CollectionUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumManagerService curriculumManagerService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    void init() {
        //初始化redis
        List<CurriculumManager> list = curriculumManagerService.getList(null, null, null, null, null, null);
        if (CollectionUtils.isNotEmpty(list)) {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            for (CurriculumManager curriculumManager : list) {
                operations.set("data:" + curriculumManager.getId(), curriculumManager.getMaxCount());
            }
        }
    }

    @GetMapping("/list")
    public CommonResult<List<CurriculumResponse>> list(@RequestParam(required = false) String search,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String code,
                                                       @RequestParam(required = false) Double credit,
                                                       @RequestParam(required = false) Integer grade,
                                                       @RequestParam(required = false) Long departmentId) {
        List<CurriculumManager> list = curriculumManagerService.getList(search, name, code, credit, grade, departmentId);
        return ResultUtil.success(this.packResponse(list));
    }

    @GetMapping("/getByClassRoomId")
    public CommonResult<List<CurriculumResponse>> getByClassRoomId(@RequestParam Long classRoomId) {
        List<CurriculumManager> list = curriculumManagerService.getByClassRoomId(classRoomId);
        return ResultUtil.success(this.packResponse(list));
    }

    @GetMapping("/getByUserId")
    public CommonResult<List<CurriculumResponse>> getByUserId(@RequestParam Long userId) {
        if (userId == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        return ResultUtil.success(this.packResponse(curriculumManagerService.getByUserId(userId)));
    }

    @GetMapping("/getByTeacherId")
    public CommonResult<List<CurriculumResponse>> getByTeacherId(@RequestParam Long teacherId) {
        if (teacherId == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        return ResultUtil.success(this.packResponse(curriculumManagerService.getByTeacherId(teacherId)));
    }

    @PostMapping("/addCurriculum")
    public CommonResult<String> addCurriculum(@RequestBody AddCurriculumRequest request) {
        if (request == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        if (curriculumManagerService.addCurriculum(request)) {
            return ResultUtil.success();
        }

        return ResultUtil.error();
    }

    @PutMapping("/updateCurriculum")
    public CommonResult<String> updateCurriculum(@RequestBody UpdateCurriculumRequest request) {
        if (request == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        if (curriculumManagerService.updateCurriculum(request)) {
            return ResultUtil.success();
        }

        return ResultUtil.error();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable("id") Long id) {
        if (id == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }
        boolean flag = curriculumManagerService.deleteCurriculumManager(id);
        if (flag) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    private List<CurriculumResponse> packResponse(List<CurriculumManager> list) {
        List<CurriculumResponse> responses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurriculumManager curriculumManager : list) {
                CurriculumResponse response = new CurriculumResponse();
                response.setId(curriculumManager.getId());
                response.setName(curriculumManager.getName());
                response.setCode(curriculumManager.getCode());
                response.setCredit(curriculumManager.getCredit());
                response.setGrade(curriculumManager.getGrade());
                response.setTeacherId(curriculumManager.getTeacherId());
                response.setTeacherName(curriculumManager.getTeacherName());
                response.setClassRoomId(curriculumManager.getClassRoomId());
                response.setClassRoomName(curriculumManager.getClassRoomName());
                response.setWeekTime(JSONObject.parseObject(curriculumManager.getWeekTime(), List.class));
                String timeDay = curriculumManager.getTimeDay();
                response.setTimeDay(JSONArray.parseArray(timeDay, List.class));
                response.setMaxCount(curriculumManager.getMaxCount());
                response.setCurCount(curriculumManager.getCurCount());
                response.setDepartmentId(curriculumManager.getDepartmentId());
                response.setDepartmentName(curriculumManager.getDepartmentName());
                response.setCreateTime(curriculumManager.getCreateTime());
                response.setUpdateTime(curriculumManager.getUpdateTime());

                responses.add(response);
            }
        }
        return responses;
    }
}
