package org.fjsei.yewu.controller;

import org.fjsei.yewu.entity.sdn.Student;
import org.fjsei.yewu.entity.sei.Teacher;
import org.fjsei.yewu.service.JpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:15
 */


//也可以在整个类 加 @RequestMapping(value = "/teacher") ：后面是基础URI

@RestController
@RequestMapping(value = "/teacher")
public class JpaTestController {

    @Autowired
    private JpaService jpaService;

    @RequestMapping("findByName/{name}")
    public Student findByName(@PathVariable String name) {
        return jpaService.findByName(name);
    }

    ///@RequestMapping("/teachers")
    @GetMapping
    public List<Teacher> getAllTeacher() {
        return jpaService.getAllTeacher();
    }

    //批处理：测试数据。

    ///@RequestMapping("/teacher/{name}")
    @GetMapping(value = "/{name}")
    public Teacher getTeacher(@PathVariable final String name) {
        return jpaService.getTeacher(name);
    }

    //实际POST测试成功： URL=  http://27.151.117.65:8673/teacher/teacher
    //Content-Type application/json; charset=utf-8   /   Cache-Control no-cache
    //请求Body = {"id":null,"name":"意查询","age":"567","course":"b是没变n2"}   ；应答体={}
    ///@Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/teacher")
    public void addTopic(@RequestBody Teacher topic) {
        jpaService.addTeacher(topic);
        //保存数据库成功
    }

}


