package com.github.peacetrue.region;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
public class Region implements Serializable {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @Id
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
