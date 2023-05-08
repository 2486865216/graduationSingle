package com.example.graduationprojectsingle.entity.curriculum_manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurriculumManager {
    /**
     * id
     */
    private Long id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程编码
     */
    private String code;

    /**
     * 学分
     */
    private Double credit;

    /**
     * 年级
     */
    private Integer grade;

    /**
     * 任课教师id
     */
    private Long teacherId;

    /**
     * 任课教师名称
     */
    private String teacherName;

    /**
     * 上课地点id
     */
    private Long classRoomId;

    /**
     * 教室名称
     */
    private String classRoomName;

    /**
     * 上课周
     */
    private String weekTime;

    /**
     * 第几节上课
     */
    private String timeDay;

    /**
     * 最大选课人数
     */
    private Integer maxCount;

    /**
     * 当前选课人数
     */
    private Integer curCount;

    /**
     * 所属院系id
     */
    private Long departmentId;

    /**
     * 所属院系名称
     */
    private String departmentName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;
}
