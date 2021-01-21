package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;
import javax.persistence.Entity;

/**旧平台4000起重机械 TB_CRANE_PARA技术参数，4800升降机 TB_LIFT_PARA 独立分开做的参数表。
*/

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Crane extends Eqp {
    //4000起重机械  TB_CRANE_PARA技术参数 ;4800升降机 TB_LIFT_PARA 独立分离的参数
    /**TB_CRANE_PARA.IF_UNNORMAL非标;*/
    private Boolean nnor;
    /**额定载重"RATED_LOAD"; CHAENGLOAMAIN{不算4D00,4800}主钩）起重机械载荷； :吨
     * 起重机械载荷(t)（收费用，原始记录里面不能用这个字段，要用MAXRATEDCARRYMASS额定起重量
     * mysql无法用load字段名
     */
    private Float rtl;
    /**额定起重量"MAXRATEDCARRYMASS"*/
    private Float cap;
    /**额定速度"RATED_V"　升降机RUNVELOCITY上行/下行额定速度区分开
     * TB_ELEV_PARA.RUNVELOCITY '运行速度(m/s),TB_CRANE_PARA.RATEDSPEED额定速度
     *有 速度　这名字字段实在太多了； vl=最主要的核心速度定义。
     */
    private Float  vl;
    /**运行速度(门座,塔式,轻小型)"RUN_V"
     * 范围/复合的 字符串
     */
    private Float  rvl;
    /**起升速度(主钩)"LIFTESPEEDMAIN"*/
    private Float  mvl;
    /**大车速度（桥门）机臂运行速度（架桥）"L_CAR_V"*/
    private Float  cvl;
    /**小车速度（桥门:旋臂）"S_CAR_V" */
    private Float  scv;
    /**横向移动速度 "LANMOVSPE"*/
    private Float  lmv;
    /**回转速度 "ROTATESVELOCITY"*/
    private Float  rtv;
    /**变幅速度 "ALTERRANGEVELOCITY",有些双数值*/
    private Float  luff;
    /**站数"STATION_NUM"*/
    private Short  ns;
    /**层数"FLOOR_NUM"  || 停车层数"TC_FLOORNUM"*/
    private Short  flo;
    /**BERNUM停车设备泊位数4D00， 泊位数量"BERNUM"*/
    private Short  pnum;
    /**合并字段 起升高度(主钩） "ELEHEIGHTMAIN"； 独立分离4800 TB_LIFT_PARA.UP_HIGH提升高度（m */
    private Float  hlf;
    /**幅度(m) "RANGE"*/
    private Float  rang;
    /**SPAN跨度（m；*/
    private Float  span;
    /**IF_TWO_CAB双司机室，*/
    private Boolean two;
    /**合并字段  设备类型不同的叫法不同/没有存储上重叠冲突只是前端显示上的差别；
     * IF_TWO_LCAR有否双小车; IF_TWO_HOIS是否双笼? 独立分离4800
    */
    private Boolean twoc;
    /**IF_GRAB_B否抓斗，*/
    private Boolean grab;
    /**IF_SUCTORIAL有否起重吸盘，*/
    private Boolean suck;
    /**IF_CONTAIN_H有否集装箱吊具，*/
    private Boolean cotr;
    /**IF_XZS否行走式,*/
    private Boolean walk;
    /**CHAADVMOM起重力矩（吨米;起重力矩"CHAADVMOM"*/
    private Float mom;
    /**IF_ZJCC否整机出厂整机上岸的;*/
    private Boolean whole;
    /**曳引机型号"DRAG_TYPE"*/
    private String  tm;
    /**曳引机编号"DRAG_COD"*/
    private String  tno;
    /**控制屏型号"CON_SCREEN_TYPE"*/
    private String  cpm;
    /**控制屏编号"CON_SCREEN_COD"*/
    private String  cpi;
    /**停车适停汽车尺寸"TC_CARSIZE"*/
    private String  pcs;
    /**停车适停汽车质量(kg)"TC_CARWEIGHT"*/
    private Float  pcw;
    /**停车单车最大进（出）时间(s)"TC_IO_MAXTIME" */
    private Float  miot;
    /**操作方式(桥门) "OPER_STYTLE" 地面+司机室*/
    private String  opm;
    /**变幅形式"ALTERRANGEMODE"*/
    private String  luf;
    /**工作级别"WORKGRADE"
     * 起重机工作级别共8种，分别是A1-A8。其中A1工作级别最低，A8工作级别最高;二种决定，其一是起重机的使用频繁程度，称为起重机利用等级；其二是起重机承受载荷的大小，称为起重机的载荷状态。
     */
    private String   jobl;
    /**停车设备高度(m)"TC_EQPHIGH", 设备外形高*/
    private Float  high;
    /**起升机构部件(桥门,轻小型,旋臂)"UP_BODY" ,钢丝绳*/
    private String  part;
    /**EQP_USE_OCCA 使用场合/适用场合{设计目标}　.EQP_USE_OCCA起重才用来敲定 ISP_CYCLE = 12
     * 从父类Eqp移动到此子类; 等待规范 Enum{中文描述的-？转成 英文graphQL接口才能对接前端的Enum} 或者前端限定文本列表输入选定/直接用String。
     * "室内,室外，吊运熔融金属，防爆，绝缘" 其中一个， "吊运熔融金属"才是有用的。
     * */
    private String occa;
    /*放入pa.json的参数有这些：
    * "是".equals(IF_METALLURGY 是否冶金(桥门)(检验))) 如果是冶金起重机，检验周期为一年用来敲定 ISP_CYCLE = 12
    *
     */

}



//升降机不可改技术参数：额定载重"RATED_LOAD" 额定速度"RATED_V"站数"STATION_NUM"曳引机编号"DRAG_COD"控制屏型号"CON_SCREEN_TYPE"控制屏编号"CON_SCREEN_COD"层数"FLOOR_NUM"曳引机型号"DRAG_TYPE"
//起重机械不可改技术参数：幅度(m) "RANGE" 停车适停汽车尺寸"TC_CARSIZE" 停车层数"TC_FLOORNUM" 大车速度（桥门）机臂运行速度（架桥）"L_CAR_V" 变幅形式"ALTERRANGEMODE" 起重力矩"CHAADVMOM" 泊位数量"BERNUM" 工作级别"WORKGRADE"
// 跨度(m)"SPAN" 额定起重量"MAXRATEDCARRYMASS" 停车适停汽车质量(kg)"TC_CARWEIGHT" 停车设备高度(m)"TC_EQPHIGH" 小车速度（桥门:旋臂）"S_CAR_V" 停车单车最大进（出）时间(s)"TC_IO_MAXTIME" 起升高度(主钩） "ELEHEIGHTMAIN"
// 起升速度(主钩)"LIFTESPEEDMAIN" 横向移动速度 "LANMOVSPE" 回转速度 "ROTATESVELOCITY" 运行速度(门座,塔式,轻小型)"RUN_V" 变幅速度 "ALTERRANGEVELOCITY" 起升机构部件(桥门,轻小型,旋臂)"UP_BODY" 操作方式(桥门) "OPER_STYTLE"

//速度:起重表UP_DOWN_SPEED=复合，PROMOTESPEED作废?　RATACTSPE就一条有数据,RATEDSPEED复合范围字符串,LIFTESPEED复合字符串，LIFTESPEEDVALUE是LIFTESPEED的主要字段浮点版。RUN_V特定设备附加{范围/数字偏大的}
