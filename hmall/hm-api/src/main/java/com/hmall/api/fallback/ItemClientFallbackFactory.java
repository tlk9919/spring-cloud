package com.hmall.api.fallback;

import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ItemClientFallbackFactory implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {

        return new ItemClient() {
            @Override
            public List<ItemDTO> queryByItems(Collection<Long> ids) {
                log.error("item-service服务调用失败-查询商品失败",cause);
                return Collections.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("item-service服务调用失败-扣减商品失败",cause);
                throw new RuntimeException("item-service",cause);
            }
        };
    }
}
