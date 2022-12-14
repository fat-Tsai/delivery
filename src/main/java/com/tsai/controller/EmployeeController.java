package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.dto.EmployeeDto;
import com.tsai.entity.Employee;
import com.tsai.service.EmployeeService;
import com.tsai.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils; // 注意：这里的包文件是apache,apache才有isNotEmpty
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录 后台管理端
     * @RequestBody 返回JSON
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<EmployeeDto> login(HttpServletRequest request, @RequestBody Employee employee ) {
        // 1. 将页面提交的密码 password 进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名 username 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到则返回登录失败结果
        if(emp == null) {
            return R.error("登录失败");
        }

        // 4. 密码比对，如果不一致返回登录失败结果
        if(!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        // 5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 6. 登录成功，将用户id存入session并返回登录成功结果  -- 废弃
//        request.getSession().setAttribute("employee",emp.getId());

        // 6.登录成功,生成JWT的令牌
        Map<String,String> map = new HashMap<>();
//        map.put("username",emp.getUsername());
        map.put("id", String.valueOf(emp.getId())); // token不能放敏感类型的信息
        // 生成token
        String token = JWTUtils.getToken(map,"pc");
        // 将token放入返回结果
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(emp,employeeDto);
        employeeDto.setToken(token);

        return R.success(employeeDto);
    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清除Session中保存的当前登录的员工id
//        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 打印日志
        log.info("新增员工，员工信息：{}",employee.toString());

        // 给员工一个初始密码 123456,先进行md5加密，再存入数据库
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 获得当前用户登录的id
        // Long empId = (Long) request.getSession().getAttribute("employee");
        // 填充其他存入数据库需要的数据
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);

        // boolean save = employeeService.save(employee);

        employeeService.save(employee);

        // 打印看看IService.save返回的是什么，应该是bool值
//        log.info("save的返回值：{}",save);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     * Page泛型根据前端页面搭配
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        // 1. 构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        // 2. 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 2-1. 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        // 2-2. 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 3. 执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        Long empId = (Long)request.getSession().getAttribute("employee");
//
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工状态修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        // 缺少PathVariable，会导致接口报错405/404，AxiosError
        Employee employee = employeeService.getById(id);
        if(employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
