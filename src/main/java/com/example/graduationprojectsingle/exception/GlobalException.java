package com.example.graduationprojectsingle.exception;

import com.example.graduationprojectsingle.Result.CommonResult;
import com.example.graduationprojectsingle.Result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException {
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public CommonResult<String> handleException(ServiceException serviceException) throws Exception {
        ConsumerException consumerException = serviceException.getConsumerException();
        log.error(consumerException.getMsg());
        return ResultUtil.error(consumerException.getCode(), consumerException.getMsg(), null);
    }
}
