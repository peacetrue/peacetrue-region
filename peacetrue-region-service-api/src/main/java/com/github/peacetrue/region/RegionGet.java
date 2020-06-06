package com.github.peacetrue.region;

import com.github.peacetrue.core.OperatorCapableImpl;
import com.github.peacetrue.validation.constraints.multinotnull.MultiNotNull;
import lombok.*;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MultiNotNull
public class RegionGet extends OperatorCapableImpl<String> {

    private static final long serialVersionUID = 0L;

    private Long id;
    private String code;

    public RegionGet(Long id) {
        this.id = id;
    }
}
