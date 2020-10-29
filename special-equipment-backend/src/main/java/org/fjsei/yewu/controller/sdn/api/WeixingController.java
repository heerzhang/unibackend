package org.fjsei.yewu.controller.sdn.api;

import md.specialEqp.Eqp;
import org.fjsei.yewu.service.sdn.WeixingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


//SDN的微信接入的控制器


@Controller
public class WeixingController {
    @Autowired
    private WeixingService weixingService;

    //二、作业人员用户认证开通接口
    //GET:使用URL来，NameValuePair方式传参数；
    // 实际!!  GET：也是可以像POST 使用RequestBody方式的。
    ///@Transactional
    @RequestMapping(method = RequestMethod.GET, value = "/sdn/api/sdn/opeMenBindSevice")
    @ResponseBody
    public WeixingOutput apiSdnOpeMenBindSevice(@RequestParam("NAME") String name, @RequestParam("ID_CARD") String idCard, @RequestParam("MOBILE") String mobile) {
       return weixingService.opeMenBindSevice(name,idCard,mobile);
    }

    //接口 一、单位账户绑定
    @RequestMapping(method = RequestMethod.POST, value = "/sdn/api/sdn/accountBindSevice")
    @ResponseBody
    public WeixingOutput apiSdnAccountBindSevice(@RequestBody WeixingInput topic) {
        return weixingService.accountBindSevice(topic);
    }

    //三、单位普通账户审核
    @RequestMapping(method = RequestMethod.POST, value = "/sdn/api/sdn/chkUntAccount")
    @ResponseBody
    public WeixingOutput apiSdnChkUntAccount(@RequestBody WeixingInput topic) {
        return weixingService.chkUntAccount(topic);
    }
    //测试:graphQl输入输出类型无法共享？
    @RequestMapping(method = RequestMethod.POST, value = "/sdn/api/sdn/test")
    @ResponseBody
    public Eqp apiSdnChkUntAccount(@RequestBody Eqp eqp) {
        //REST和graphQL不同点！
        //REST的输入输出对象都可以不受到任何控制；可以共用，能直接把对象以及关联对象全部地序列化或者反序列化，
        //graphQL输入对象必须另外建立，好处坏处？。
        Eqp topic=new Eqp();
        topic=eqp;
        return topic;
    }
}

