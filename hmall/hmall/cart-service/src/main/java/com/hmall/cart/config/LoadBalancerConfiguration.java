package com.hmall.cart.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;

public class LoadBalancerConfiguration {
    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
        Environment environment,
        NacosDiscoveryProperties properties,
        LoadBalancerClientFactory loadBalancerClientFactory) {
        
        // 获取服务名称(ServiceId)
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        
        // 创建并返回Nacos专用的负载均衡器
        return new NacosLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name,
            properties
        );
    }
}