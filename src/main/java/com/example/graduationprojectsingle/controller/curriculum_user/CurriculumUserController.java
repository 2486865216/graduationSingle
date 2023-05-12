package com.example.graduationprojectsingle.controller.curriculum_user;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import com.example.graduationprojectsingle.entity.curriculum_user.CurriculumUser;
import com.example.graduationprojectsingle.entity.curriculum_user.req.AddCurriculumUserRequest;
import com.example.graduationprojectsingle.entity.curriculum_user.req.DeleteCurriculumUserRequest;
import com.example.graduationprojectsingle.exception.ConsumerException;
import com.example.graduationprojectsingle.exception.ServiceException;
import com.example.graduationprojectsingle.service.curriculum_user.CurriculumUserService;
import com.example.graduationprojectsingle.utils.stringUtils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/curriculum/user")
@Slf4j
public class CurriculumUserController {

    @Autowired
    private CurriculumUserService curriculumUserService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/list")
    public CommonResult<List<CurriculumUser>> list(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long curriculumId) {
        return ResultUtil.success(curriculumUserService.getList(userId, curriculumId));
    }

    @PostMapping("/addCurriculumUser")
    public CommonResult<String> addCurriculumUser(@RequestBody AddCurriculumUserRequest request) {
        if (request == null || request.getUserId() == null || request.getCurriculumId() == null) {
            throw new ServiceException(ConsumerException.INVALID_PARAMS);
        }

        //redis分布式锁
        String REDIS_DATA_KEY = "data:" + request.getCurriculumId();
        //生成唯一标识
        String value = UUID.randomUUID().toString().replace("-", "");
        String REDIS_LOCK_KEY = "lock";
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        try {
            Boolean ifAbsent = operations.setIfAbsent(REDIS_LOCK_KEY, value, 10, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(ifAbsent)) {
                throw new ServiceException(ConsumerException.REDIS_LOCK_FAILURE);
            }

            String result = String.valueOf(operations.get(REDIS_DATA_KEY));
            if (StringUtils.isNotEmpty(result) && !"null".equals(result)) {
                int total = Integer.parseInt(result);
                if (total > 0) {
                    int realTotal = total - 1;
                    operations.set(REDIS_DATA_KEY, realTotal);
                    boolean flag = curriculumUserService.addCurriculumUser(request.getUserId(), request.getCurriculumId());
                    if (flag) {
                        return ResultUtil.success();
                    }
                    return ResultUtil.error();
                } else {
                    throw new ServiceException(ConsumerException.REDIS_LOCK_FAILURE);
                }
            } else {
                throw new ServiceException(ConsumerException.REDIS_LOCK_FAILURE);
            }
        } finally {
            //删除自己添加的锁
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                    "then " +
                    "return redis.call('del',KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";
            Object execute = redisTemplate.execute(RedisScript.of(script), Collections.singletonList(REDIS_LOCK_KEY), value);
            if ("1".equals(String.valueOf(execute))) {
                log.info("del lock success!");
            } else {
                log.error("del local failure!");
            }
        }
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
