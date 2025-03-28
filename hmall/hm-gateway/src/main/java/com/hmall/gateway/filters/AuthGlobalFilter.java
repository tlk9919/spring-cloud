package com.hmall.gateway.filters;


import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request
        ServerHttpRequest request = exchange.getRequest();
        //2.判断是否需要登录拦截、
        if (isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }

        //3.判断token是否合法
        String token=null;
        Long userId=null;
        List<String> authorization = request.getHeaders().get("authorization");
        if (authorization!=null && !authorization.isEmpty()){
             token = authorization.get(0);
        }
        //3.1校验token
        try {
             userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            //拦截，返回状态为401
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String userInfo = userId.toString();
        //4. 传递用户信息
        ServerWebExchange serverWebExchange = exchange.mutate()
                .request(builder -> {
                    builder.header("user-info", userInfo);
                }).build();
        System.out.println("userId:"+userId);

        //5.放行
        return chain.filter(serverWebExchange);
    }

    private boolean isExclude(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern,path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
