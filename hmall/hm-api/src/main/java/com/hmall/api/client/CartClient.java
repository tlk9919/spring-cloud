package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Collection;

@FeignClient("cart-service")
public interface CartClient {
    @DeleteMapping
    void removeByItemIds(Collection<Long> itemIds);
}
