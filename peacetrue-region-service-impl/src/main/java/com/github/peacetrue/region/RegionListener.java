package com.github.peacetrue.region;

import com.github.peacetrue.core.Operators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

/**
 * @author : xiayx
 * @since : 2020-12-06 08:10
 **/
@Slf4j
@Component
public class RegionListener {

    @Autowired
    private RegionService regionService;

    @EventListener
    public void setParentLeafAfterRegionAdd(PayloadApplicationEvent<RegionAdd> event) {
        RegionVO source = (RegionVO) event.getSource();
        log.info("新增地区节点[{}]后，设置父节点是否叶子节点", source.getId());
        //如果父节点已经不是叶子节点，不需要再设置
        RegionGet regionGet = new RegionGet(source.getParentId(), true);
        regionService.get(Operators.setOperator(event.getPayload(), regionGet))
                .flatMap(dictionaryTypeVO -> {
                    RegionModify regionModify = new RegionModify();
                    Operators.setOperator(event.getPayload(), regionModify);
                    regionModify.setId(dictionaryTypeVO.getId());
                    regionModify.setLeaf(false);
                    return regionService.modify(regionModify);
                })
                .publishOn(Schedulers.elastic())
                .subscribe();
    }
}
