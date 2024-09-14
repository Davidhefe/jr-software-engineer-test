package com.adobe.bookstore.service;

import com.adobe.bookstore.DO.OrderItemDO;

import java.util.List;
import java.util.Map;

public interface IBookOrderService {

    String createOrder(List<OrderItemDO> order);

    Map<String, List<OrderItemDO>> getOrders();

}
