package com.pamirs.takin.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;

@Data
public class WarnDetail {

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private Date warnTime;

    private Date createTime;

}
