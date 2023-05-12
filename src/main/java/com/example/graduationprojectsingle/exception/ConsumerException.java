package com.example.graduationprojectsingle.exception;

public enum ConsumerException {
    //登录相关
    USERNAME_ERROR(500, "用户名错误!"),
    PASSWORD_ERROR(500, "密码错误!"),
    AUTHORIZATION_USER_ERROR(505, "未登录或者登录过期!"),
    REDIS_LOCK_FAILURE(456, "选课失败!"),

    //请求参数异常
    INVALID_PARAMS(500, "非法的请求参数!"),

    //调用服务异常
    REMOTE_SERVICE_EXCEPTION(500, "远程调用服务异常!"),
    DEPARTMENT_NAME_REPEAT(500, "院系名称重复!"),
    DEPARTMENT_CODE_REPEAT(500, "院系编码重复!"),
    DEPARTMENT_NOT_EXIST(500, "院系不存在!"),
    PROFESSIONAL_NAME_REPEAT(500, "专业名称重复!"),
    PROFESSIONAL_CODE_REPEAT(500, "专业编码重复!"),
    PROFESSIONAL_NOT_EXIST(500, "专业不存在!"),
    CLASS_NAME_REPEAT(500, "班级名称重复!"),
    CLASS_CODE_REPEAT(500, "班级编码重复!"),
    CLASS_NOT_EXIST(500, "班级不存在!"),
    USERNAME_HAS_EXIST(500, "用户名已存在!"),
    USER_NOT_EXIST(500, "用户不存在!"),
    CURRICULUM_NAME_EXIST(500, "课程名称重复!"),
    CURRICULUM_CODE_EXIST(500, "课程编码重复!"),
    CURRICULUM_NOT_EXIST(500, "课程不存在!"),
    CLASS_ROOM_NOT_EXIST(500, "教室不存在!"),
    TEACHER_TIME_ERROR(500, "该教师的上课时间有冲突，请重新选择上课时间"),
    CLASS_ROOM_TIME_ERROR(500, "该教室的上课时间有冲突，请重新选择上课时间"),
    ;
    private Integer code;
    private String msg;

    ConsumerException(Integer code, String msg) {
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
