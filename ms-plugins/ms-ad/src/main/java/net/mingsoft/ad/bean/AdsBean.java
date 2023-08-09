/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */



package net.mingsoft.ad.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.mingsoft.ad.entity.AdsEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 钟业海
 *  2019/11/23
 */
public class AdsBean extends AdsEntity {
    /**
     * 广告位名称
     */
    private String positionName;

    /**
     * 用于查询包含这个时间的广告
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date queryDate;

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }
}
