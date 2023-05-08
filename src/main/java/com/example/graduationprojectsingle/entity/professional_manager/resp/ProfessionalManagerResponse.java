package com.example.graduationprojectsingle.entity.professional_manager.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfessionalManagerResponse {
    /**
     * id
     */
    private Long id;

    /**
     * 专业名称
     */
    private String name;

    /**
     * 专业编码
     */
    private String code;

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
}
