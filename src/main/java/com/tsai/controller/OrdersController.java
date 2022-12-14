package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.dto.SetmealDto;
import com.tsai.entity.Category;
import com.tsai.entity.Orders;
import com.tsai.entity.Setmeal;
import com.tsai.entity.ShoppingCart;
import com.tsai.service.OrdersService;
import com.tsai.utils.JWTUtils;
import org.springframework.beans.BeanUtils;
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

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<Orders> submit(@RequestBody Orders orders, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        orders.setUserId(userId);
        Orders order = ordersService.submit(orders);
        return R.success(order);
    }

    /**
     * 支付订单
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
     * @param request
     * @return
     */
    @GetMapping("/list")
    public R<List> list(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        // 获取该用户的所有订单数据
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        List<Orders> list = ordersService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 查询指定订单的信息
     * @param orderId
     * @return
     */
    @GetMapping("")
    public R<Orders> getOrder(String orderId) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,orderId);
        Orders orders = ordersService.getOne(queryWrapper);
        return R.success(orders);
    }

    /**
     * 获取订单列表
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String id,String date, String phone) {
        System.out.println("id是啥："+id);
        System.out.println("date是啥"+date);
        System.out.println("phone是啥"+phone);
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(id!= null,Orders::getId,id);
        queryWrapper.like(date!=null,Orders::getOrderTime,date);
        queryWrapper.like(phone!=null,Orders::getPhone,phone);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }



}
