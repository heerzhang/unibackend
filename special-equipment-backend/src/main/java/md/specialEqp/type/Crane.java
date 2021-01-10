package md.specialEqp.type;

import lombok.*;
import lombok.experimental.SuperBuilder;
import md.specialEqp.Eqp;
import javax.persistence.Entity;
//旧平台4000起重机械 TB_CRANE_PARA技术参数，4800升降机 TB_LIFT_PARA 独立分开做的参数表。


@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Crane extends Eqp {
    //4000起重机械  TB_CRANE_PARA技术参数 ;4800升降机 TB_LIFT_PARA 独立分离的参数
    private Boolean nnor;  //TB_CRANE_PARA.IF_UNNORMAL非标;
    //额定载重"RATED_LOAD"
    private Float load; //CHAENGLOAMAIN{不算4D00,4800}主钩）起重机械载荷； :吨
    private Float cap;  //额定起重量"MAXRATEDCARRYMASS"
    //额定速度"RATED_V"
    private Float  vl;
    //大车速度（桥门）机臂运行速度（架桥）"L_CAR_V"
    private Float  cvl;
    //站数"STATION_NUM"
    private Short ns;
    //层数"FLOOR_NUM"  || 停车层数"TC_FLOORNUM"
    private Short flo;
    private Short pnum; //BERNUM停车设备泊位数4D00， 泊位数量"BERNUM"
    //合并字段  起重机械载荷(t)（收费用，原始记录里面不能用这个字段，要用MAXRATEDCARRYMASS额定起重量
    private Float hlf; //ELEHEIGHTMAIN主钩）起升高度； 独立分离4800 TB_LIFT_PARA.UP_HIGH提升高度（m
    //幅度(m) "RANGE"
    private Float  rang;
    private Float  span; //SPAN跨度（m；
    private Boolean two; //IF_TWO_CAB双司机室，
    //合并字段  设备类型不同的叫法不同/没有存储上重叠冲突只是前端显示上的差别；
    private Boolean twoc; //IF_TWO_LCAR有否双小车; IF_TWO_HOIS是否双笼? 独立分离4800
    private Boolean grab; //IF_GRAB_B否抓斗，
    private Boolean suck; //IF_SUCTORIAL有否起重吸盘，
    private Boolean cotr; //IF_CONTAIN_H否集装箱吊具，
    private Boolean walk;  //IF_XZS否行走式,
    private Float mom; //CHAADVMOM起重力矩（吨米;起重力矩"CHAADVMOM"
    private Boolean whole; //IF_ZJCC否整机出厂整机上岸的;
    //曳引机编号"DRAG_COD"
    private String  tno;
    //控制屏型号"CON_SCREEN_TYPE"
    private String  cpm;
    //控制屏编号"CON_SCREEN_COD"
    private String  cpi;
    //曳引机型号"DRAG_TYPE"
    private String  tm;
    //停车适停汽车尺寸"TC_CARSIZE"
    private String  pcs;
    private Float  pcw;  //停车适停汽车质量(kg)"TC_CARWEIGHT"
    //变幅形式"ALTERRANGEMODE"
    private String  luf;
    //工作级别"WORKGRADE"
    private String   jobl;
}


//升降机不可改技术参数：额定载重"RATED_LOAD" 额定速度"RATED_V"站数"STATION_NUM"曳引机编号"DRAG_COD"控制屏型号"CON_SCREEN_TYPE"控制屏编号"CON_SCREEN_COD"层数"FLOOR_NUM"曳引机型号"DRAG_TYPE"
//起重机械不可改技术参数：幅度(m) "RANGE" 停车适停汽车尺寸"TC_CARSIZE" 停车层数"TC_FLOORNUM" 大车速度（桥门）机臂运行速度（架桥）"L_CAR_V" 变幅形式"ALTERRANGEMODE" 起重力矩"CHAADVMOM" 泊位数量"BERNUM" 工作级别"WORKGRADE"
// 跨度(m)"SPAN" 额定起重量"MAXRATEDCARRYMASS" 停车适停汽车质量(kg)"TC_CARWEIGHT" 停车设备高度(m)"TC_EQPHIGH" 小车速度（桥门:旋臂）"S_CAR_V" 停车单车最大进（出）时间(s)"TC_IO_MAXTIME" 起升高度(主钩） "ELEHEIGHTMAIN"
// 起升速度(主钩)"LIFTESPEEDMAIN" 横向移动速度 "LANMOVSPE" 回转速度 "ROTATESVELOCITY" 运行速度(门座,塔式,轻小型)"RUN_V" 变幅速度 "ALTERRANGEVELOCITY" 起升机构部件(桥门,轻小型,旋臂)"UP_BODY" 操作方式(桥门) "OPER_STYTLE"

