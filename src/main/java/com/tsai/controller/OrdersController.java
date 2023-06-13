package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.entity.*;
import com.tsai.service.OrderDetailService;
import com.tsai.service.OrdersService;
import com.tsai.service.ShoppingCartService;
import com.tsai.utils.JWTUtils;
import com.tsai.utils.MyDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单,创建订单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<Orders> submit(@RequestBody Orders orders, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        orders.setUserId(userId);
        Orders order = ordersService.submit(orders);
        return R.success(order);
    }

    /**
     * 支付订单
     *
     * @param id
     * @return
     */
    @PostMapping("/pay")
    public R<String> pay(Long id) {
        Orders orders = ordersService.getById(id);
        orders.setStatus(2);
        orders.setCheckoutTime(LocalDateTime.now());
        ordersService.updateById(orders);
        return R.success("支付成功");
    }

    /**
     * 获取某个用户的所有订单信息
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public R<List> list(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        // 获取该用户的所有订单数据
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        List<Orders> list = ordersService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 查询指定订单的信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("")
    public R<Orders> getOrder(String orderId) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId, orderId);
        Orders orders = ordersService.getOne(queryWrapper);
        return R.success(orders);
    }

    /**
     * 获取订单列表
     *
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String id, String date, String phone) {
        System.out.println("id是啥：" + id);
        System.out.println("date是啥" + date);
        System.out.println("phone是啥" + phone);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(id != null, Orders::getId, id);
        queryWrapper.like(date != null, Orders::getOrderTime, date);
        queryWrapper.like(phone != null, Orders::getPhone, phone);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 获取订单类型数量
     *
     * @return
     */
    @GetMapping("/type")
    public R<Integer> getTypeNum(Integer type, String time) {
//        public R<Integer> getTypeNum(Integer type, String time) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Orders::getOrderTime, time);
        queryWrapper.eq(Orders::getType, type);
        int count = ordersService.count(queryWrapper);
        return R.success(count);
    }


    /**
     * 获取七日以来的订单数量
     *
     * @return
     */
    @GetMapping("getSevenDate")
    public R<List> countSeven() {
        List<String> sevenDate = MyDateUtils.getSevenDate();
        List<BackOrder> collect = sevenDate.stream().map((item) -> {
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Orders::getStatus, 2); // 获取已经支付成功的订单
            queryWrapper.like(Orders::getOrderTime, item);
            int count = ordersService.count(queryWrapper);
            System.out.println(item);
            double benefit = ordersService.sumAll(item);
            BackOrder backOrder = new BackOrder();
            backOrder.setTime(item);
            backOrder.setNum(count);
            backOrder.setBenefit(benefit);
            return backOrder;
        }).collect(Collectors.toList());
//        }).collect(Collectors.toList());

        return R.success(collect);
    }

    @PostMapping("/getAnother")
    public R<String> getAnother(@RequestBody Orders order) {
        // 1.获取订单的菜品信息 2.添加到购物车 3.前端跳转支付
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,order.getId());
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        List<Object> collect = list.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(orderDetail.getName()); // 菜品名称
            shoppingCart.setImage(orderDetail.getImage()); // 图片
            shoppingCart.setUserId(order.getUserId()); // 添加用户
            shoppingCart.setSetmealId(orderDetail.getSetmealId()); // 添加菜品
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor()); //添加口味
            shoppingCart.setNumber(orderDetail.getNumber()); // 添加数量
            shoppingCart.setAmount(orderDetail.getAmount()); // 添加价格
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            return null;
        }).collect(Collectors.toList());
        return R.success("添加成功");
    }
}
