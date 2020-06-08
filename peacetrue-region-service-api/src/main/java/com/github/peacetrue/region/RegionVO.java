package com.github.peacetrue.region;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author xiayx
 */
@Data
@ToString
public class RegionVO implements Serializable {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    private Long id;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 备注 */
    private String remark;
    /** 上一级主键 */
    private Long parentId;
}
