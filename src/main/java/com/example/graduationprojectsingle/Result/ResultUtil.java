package com.example.graduationprojectsingle.Result;

public class ResultUtil {
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(StateCode.SUCCESS.getCode(), StateCode.SUCCESS.getMsg(), null);
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(StateCode.SUCCESS.getCode(), StateCode.SUCCESS.getMsg(), data);
    }

    public static <T> CommonResult<T> error() {
        return new CommonResult<>(StateCode.ERROR.getCode(), StateCode.ERROR.getMsg(), null);
    }

    public static <T> CommonResult<T> error(String msg) {
        return new CommonResult<>(StateCode.ERROR.getCode(), msg, null);
    }

    public static <T> CommonResult<T> error(Integer code, String msg, T data) {
        return new CommonResult<>(code, msg, data);
    }
}
