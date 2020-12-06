package com.github.peacetrue.region;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.peacetrue.spring.util.BeanUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : xiayx
 * @since : 2020-05-23 17:21
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        R2dbcAutoConfiguration.class,
        R2dbcDataAutoConfiguration.class,
        R2dbcRepositoriesAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class,
})
@EnableAutoConfiguration
public class DataInit {

    @Autowired
    private RegionService regionService;
    private ObjectMapper objectMapper = new ObjectMapper();

    //    @Test
    void init() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/2020年1月中华人民共和国县以上行政区划代码.json");
        CollectionType valueType = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Province.class);
        List<Province> provinces = objectMapper.readValue(resourceAsStream, valueType);
        for (Province province : provinces) {
            RegionAdd provinceAdd = BeanUtils.map(province, RegionAdd.class);
            RegionVO provinceVO = regionService.add(provinceAdd).block();
            if (province.getCityList() == null) continue;
            for (City city : province.getCityList()) {
                RegionAdd cityAdd = BeanUtils.map(city, RegionAdd.class);
                cityAdd.setParentId(Objects.requireNonNull(provinceVO.getId()));
                RegionVO cityVO = regionService.add(cityAdd).block();
                if (city.getAreaList() == null) continue;
                for (Region region : city.getAreaList()) {
                    RegionAdd regionAdd = BeanUtils.map(region, RegionAdd.class);
                    regionAdd.setParentId(Objects.requireNonNull(cityVO.getId()));
                    RegionVO regionVO = regionService.add(regionAdd).block();
                }
            }
        }
    }

    @Getter
    @Setter
    public static class Region {
        private String code;
        private String name;
    }

    @Getter
    @Setter
    public static class Province extends Region {
        private List<City> cityList;
    }

    @Getter
    @Setter
    public static class City extends Region {
        private List<Region> areaList;
    }
}
