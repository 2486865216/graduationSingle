package com.example.graduationprojectsingle.Result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommonResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3917077133272065948L;
    private Integer code;
    private String msg;
    private T data;

    public CommonResult() {}

    public CommonResult(Integer code, String msg, T data) {
        this.code=  code;
        this.msg = msg;
        this.data = data;
    }
}
