package com.funny.config;


import com.funny.admin.agent.entity.ThridPddConfigEntity;
import com.funny.admin.agent.service.ThridPddConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liyanjun
 */
@Component
public class PddConfig implements InitializingBean {

    @Autowired
    private ThridPddConfigService thridPddConfigService;

    @Override
    public void afterPropertiesSet() {
        List<ThridPddConfigEntity> thridPddConfigEntities = thridPddConfigService.queryList(null);
        for (ThridPddConfigEntity config : thridPddConfigEntities) {

        }
    }
}
