package org.fjsei.yewu.controller.sdn.api;

import com.google.common.collect.Lists;
import org.fjsei.yewu.entity.sdn.TSdnEnp;

import java.util.List;

//非实体对象的接口形式拟用输出适配器
public class WeixingOutput {
    public String returnCode;
    public String returnDesc;
    public List<WeixingUser> data;  //数组

    public WeixingOutput copyDataFromTSdnEnp(TSdnEnp from, String UNT_ID, String UNT_ORG_COD) {
        if(from==null)     return this;
        WeixingUser data=new WeixingUser();
        data.USER_ID=from.getTebmuser().getUserId().toString();
        data.STATUS=from.getTebmuser().getStatus();
        data.USER_TYPE=from.getTebmuser().getUserType();
       //  data.UNT_ID= UNT_ORG_COD  从检验平台的数据库查
        // outObj.data.add(data);
        data.UNT_ID=UNT_ID;
        data.UNT_ORG_COD=UNT_ORG_COD;
        this.data = Lists.newArrayList();
        this.data.add(data);
       //outObj.returnCode="48474";
        return this;
    }

}

