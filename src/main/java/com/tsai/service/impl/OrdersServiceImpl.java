package com.tsai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.common.CustomException;
import com.tsai.entity.OrderDetail;
import com.tsai.entity.Orders;
import com.tsai.entity.ShoppingCart;
import com.tsai.entity.User;
import com.tsai.mapper.OrdersMapper;
import com.tsai.service.OrderDetailService;
import com.tsai.service.OrdersService;
import com.tsai.service.ShoppingCartService;
import com.tsai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;


    @Override
    public Orders submit(Orders orders) {
        Long userId = orders.getUserId();

        // 获取购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        // 获取订单类型:1-自取，2-外卖
        Integer type = orders.getType();
        Long addressBookId = orders.getAddressBookId();
        if(type == 2) {
            if (addressBookId == null) {
                throw new CustomException("用户地址信息有误，不能下单");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("今天是"+today);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Orders::getOrderTime,today);
        int count = ordersService.count(lambdaQueryWrapper);


        // 生成订单号
        long orderId = IdWorker.getId();
        // 设置订单号和下单时间
        orders.setId(orderId);
        orders.setOrderTime(now);
        orders.setCode(String.format("%04d",(count+1)));

        AtomicInteger amount = new AtomicInteger(0);

        // 订单详情信息
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            // 计算订单的总支付金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setStatus(1);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
//        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));



//        if(type == 2) {
            // 获取用户信息
//            User user = userService.getById(userId);
//            orders.setUserName(user.getName());
//            orders.setConsignee(addressBook.getConsignee());
//            orders.setPhone(addressBook.getPhone());
//            orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
//                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
//                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
//                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
//        }


        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
        return orders;
    }

    @Override
    public double sumAll(String time) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Orders::getOrderTime, time);
        List<Orders> list = ordersService.list(queryWrapper);

        AtomicInteger sum = new AtomicInteger(0);
        for (Orders order : list) {
            sum.addAndGet(order.getAmount().intValue());
        }

        return sum.intValue();
    }
}
