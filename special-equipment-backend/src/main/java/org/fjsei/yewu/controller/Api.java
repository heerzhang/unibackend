package org.fjsei.yewu.controller;

import md.specialEqp.Eqp;
import org.fjsei.yewu.entity.sdn.TestBean;
import org.fjsei.yewu.service.core.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Api {
    @Autowired
    private RestService restService;

    @GetMapping("/")
    public String hello(){
        return "hello 测试 world";
    }

    /*
     * {
     *     "framwork":"SPRINGDATA",
	 *     "xxxx":"干扰用"
     *  }
     */
    @PostMapping("/enum")
    public TestBean getEnum(@RequestBody TestBean testBean){
        System.out.println(testBean.toString());
        return testBean;
    }

    //注意：　/test/   和 /test 是两个不同的URL

    @GetMapping("/test/")
    public String findEQPByName(){
        Eqp eqp=restService.findByName("电梯AT032039");
        return eqp.toString();
    }
    @RequestMapping("/test/{name}")
    public String findEQPByName(@PathVariable String name) {
        Eqp eqp=restService.findByName(name);
        return eqp!=null?eqp.toString():"没找到";
    }
}


//stream三点特性.stream不存储数据.stream不改变源数据.stream延迟执行特性; https://www.cnblogs.com/andywithu/p/7404101.html
