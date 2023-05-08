package com.example.graduationprojectsingle.entity.curriculum_manager.req;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCurriculumRequest {
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
    private Integer grade = 1;

    /**
     * 任课教师id
     */
    private Long teacherId;

    /**
     * 上课地点id
     */
    private Long classRoomId;

    /**
     * 上课周
     */
    private List<Integer> weekTime;

    /**
     * 第几节上课
     */
    private List<List<Integer>> timeDay;

    /**
     * 最大选课人数
     */
    private Integer maxCount;

    /**
     * 所属院系id
     */
    private Long departmentId;
}
