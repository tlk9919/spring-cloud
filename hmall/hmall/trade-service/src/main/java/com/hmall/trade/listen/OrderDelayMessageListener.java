package com.hmall.trade.listen;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constants.MQConstants;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {
    private final IOrderService orderService;
    private final PayClient payClient;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.DELAY_ORDER_QUEUE_NAME),
            exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME,type = ExchangeTypes.DIRECT,delayed = "true"),
            key = MQConstants.DELAY_ORDER_KEY
    ))
    public void listenOrderDelayMessage(Long orderId){
        //1.查询订单
        Order order = orderService.getById(orderId);
        //2.判断订单状态，是否为未支付
        if (order==null || order.getStatus()!=1)
        {//返回
            return;
        }
        //3.未支付查询流水
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);

        //4.判断支付状态
        if (payOrder != null && payOrder.getStatus()==3) {
            //5.已经支付，修改订单状态为已支付
            orderService.markOrderPaySuccess(orderId);
        }
        else {
            //6.未支付，修改订单状态为已取消，恢复库存
            orderService.cancelOrder(orderId);
        }
    }
}
