package com.example.graduationprojectsingle.Result;

public enum StateCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败");

    private Integer code;
    private String msg;

    StateCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
