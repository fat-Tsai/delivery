package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsai.common.R;
import com.tsai.entity.ShoppingCart;
import com.tsai.service.ShoppingCartService;
import com.tsai.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        // 获取 userId
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        shoppingCart.setUserId(userId);
        System.out.println("userId"+userId);

        String dishFlavor = shoppingCart.getDishFlavor();

        // 设置查询条件： 根据用户id、菜品id|套餐id、口味数据 去查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.eq(dishFlavor != null, ShoppingCart::getDishFlavor, dishFlavor);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 查询当前菜品套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            // 信息存在
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 新增购物车记录
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 从购物车删除
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long userId = shoppingCart.getUserId();
        Long dishId = shoppingCart.getDishId();

        // 设置查询条件： 根据用户id、菜品id|套餐id、口味数据 去查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId != null) {
            String dishFlavor = shoppingCart.getDishFlavor();
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            queryWrapper.eq(dishFlavor != null, ShoppingCart::getDishFlavor, dishFlavor);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 查询当前菜品套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne.getNumber() == 1) {
            shoppingCartService.removeById(cartServiceOne);
        }
        Integer number = cartServiceOne.getNumber();
        shoppingCart.setNumber(number-1);
        shoppingCartService.updateById(shoppingCart);
        cartServiceOne = shoppingCart;
        return R.success(cartServiceOne);
    }

    /**
     * 获取购物车列表数据
     * @return
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        // 获取该用户的所有购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清除购物车
     * @return
     */
    @PostMapping("/clear")
    public R<String> clear(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
}
