package com.hmall.gateway.routes;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouteLoader {
    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;
    private final String dataId = "hmall-gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds=new LinkedHashSet<>();
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        //1.项目启动时，先拉取一下配置，并监听配置的更新
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        //2. 监听到配置更新，更新路由表
                        updateConfigInfo(configInfo);
                    }


                });
        //3. 第一次读取到配置，也需要加载到路由表
        updateConfigInfo(configInfo);
    }
    private void updateConfigInfo(String configInfo) {
        log.debug("监听到配置更新，更新路由表：{}", configInfo);
        //1.解析配置信息，转为RouteDefinition对象
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.删除旧的路由表
        routeIds.forEach(routeId -> {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        });
        routeIds.clear();

        //3.更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            log.info("更新路由表：{}", routeDefinition);
            //3.1更新路由表
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            //3.2记录旧的路由id，便于下一次直接删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
