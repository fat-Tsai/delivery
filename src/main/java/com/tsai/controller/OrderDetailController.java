package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsai.common.R;
import com.tsai.entity.OrderDetail;
import com.tsai.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 获取某个订单下的所有菜品图片列表
     *
     * @param orderId
     * @return
     */
    @GetMapping("imgList")
    public R<List> imgList(String orderId) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, orderId);
        System.out.println("订单id：" + orderId);
        List<OrderDetail> orderDetails = orderDetailService.list(wrapper);
        System.out.println("订单列表：" + orderDetails);
        List<String> list = orderDetails.stream().map((item) -> {
            String imgUrl = item.getImage();
            return imgUrl;
        }).collect(Collectors.toList());
        System.out.println("图片列表：" + list);
        return R.success(list);
    }

    @GetMapping("/list")
    public R<List> list(String orderId) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> list = orderDetailService.list(wrapper);
        return R.success(list);
    }
}
