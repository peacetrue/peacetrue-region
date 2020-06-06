package com.github.peacetrue.region;

import com.github.peacetrue.core.OperatorCapableImpl;
import com.github.peacetrue.core.Range;
import lombok.*;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegionQuery extends OperatorCapableImpl<String> {

    public static final RegionQuery DEFAULT = new RegionQuery();

    private static final long serialVersionUID = 0L;

    /** 主键 */
    private Long[] id;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 上一级主键 */
    private Long parentId;
    private Range.Date createdTime;

    public RegionQuery(Long[] id) {
        this.id = id;
    }

}
