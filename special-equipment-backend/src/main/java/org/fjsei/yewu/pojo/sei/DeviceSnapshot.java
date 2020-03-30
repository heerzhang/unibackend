package org.fjsei.yewu.pojo.sei;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//仅供服务端使用，非DB表实体的。但是从数据库字段转换，然后转输出变JSON发给前端，需要类比JPA数据库5种基本类型。
//提供前端，但是字段若是null的如何合并 能够理解？

@Getter
@Setter
@NoArgsConstructor
public class DeviceSnapshot {
    private String eqpcod;
    private String 监察识别码;    //OIDNO;
    private String 使用证号;     //'使用证号'
    private String 设备代码;     //'设备代码
    private String 设备品种;            //'设备名称'=品种    EQP_VART_NAME
    private String 设备类别;        //设备类别  EQP_SORT_NAME
    private String 型号;      //型号'
    private String 出厂编号;     //出厂编号；
    private String 单位内部编号;     //单位内部编号'
    private String  制造日期;   //制造日期
    private String  使用单位;       // ,   USE_UNT_ADDR
    private String  使用单位地址;
    private String  分支机构;
    private String  分支机构地址;
    private String  制造单位;
    private String  改造单位;
    private String  维保单位;
    private String  楼盘;
    private String  楼盘地址;
    private String  改造日期;    //改造日期'
    private String  下检日期;  //1下次检验日期1（在线、年度）
         //2下次检验日期2(机电定检，内检，全面）'
    private String  设备使用地点;

    //'控制方式@[{id:''按钮'',text:''按钮''},{id:''信号'',text:''信号''},{id:''集选'',text:''集选''},{id:''并联'',text:''并联''},{id:''群控'',text:''群控''}]'
    private String 控制方式;
    private Long 电梯层数;     //'电梯层数'
    private Long 电梯站数;     // '电梯站数'
    //类型不对； private int 电梯门数;     数据库可能为null?
    private Long 电梯门数;       // is '电梯门数'
    private Double  运行速度;      //    is '运行速度(m/s)
    private Double  额定载荷;       //   is '额定载荷(kg) .

}

