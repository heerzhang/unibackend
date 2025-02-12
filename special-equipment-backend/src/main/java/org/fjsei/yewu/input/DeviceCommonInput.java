package org.fjsei.yewu.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
//graphQL内省和接口参数输入的场合，实际只用Getter就可以。
//[语法核心限制] 查询和变更Query, Mutation的接口输入的参数类型不可以用外模型object对象类型=type定义的啊！！。
//Input Object：输入对象 定义的数据类型，因为 Object 的字段可能存在循环引用a->b->a，可以嵌套，但是不能死循环，或者字段引用了不能作为查询输入对象的接口和联合类型。
//因为type普通对象直接用于输入参数的话,语义比较模糊,所以graphQL要求另外单独建
//都是String类型的，input弱类型化。 前端往后端这边的输入数据接口直接json/无类型，无需要区分/数值/日期/bool/字符Byte/字符串。
//弱类型String顺带可以鉴别null还是""的区分，能强化语义场景。
//规则：没有在input当中出现的属性字段名，那它就省略掉，就是不知道有没有变化;

//特别注意！变量取名有限制的！若是fNo就无法解析出取值，必须修改为fno才可肯通行正常的；第二个字母大写特别注意，不允许？？。
/** 专用于前端输入参数，输入类似REST的搞的DTO，graphql接口函数参数不能直接上实体类。
 * 没办法直接用Eqp的实体类，报错.SchemaError: Expected type 'Eqp' to be a GraphQLInputType, but it wasn't!
*/

@Getter
@Setter
public class DeviceCommonInput {
    //这里字段名字必须和外模型相同的。
    String  type;
    String  sort;
    String  vart;
    String  cod;
    String  address;
    String  oid;
    String   lat;
    String   fno;   //原先预定是fNo变量名字的，无法正常！只好修改成fno来通过底层接口graphQL的解析层。
    Long    ownerId;    //产权人的单位ID
    String  cert;
    String  addr;
    String  model;
    Long    useUid;     //使用单位ID
    Long  id;       //共通ID
    //共用的 json类型 汇聚字段。
    String  svp;     //数据库存储可以null或"{}" 但是不可以是""，否则前端报错。json里面有没有以及各字段类型只能前端自己做主了。不受到控制。
    //可信赖的前端用户，用户自己负责任的参数字段，伪造变造可能，和权限适配管理。
    String  pa;
    //电梯的：
    Short  flo;
    Boolean spec;
    Boolean nnor;
    Boolean oldb;
    Float  vl;
    Float  hlf;
    Float  lesc;
    Float  wesc;
    String  cpm;
    String  tm;
    String  mtm;
    String  buff;
    Integer rtl;
    String aap;
    String  prot;
    String  doop;
    String  limm;
    String  opm;
    Date lbkd;
    Date nbkd;
    //事故隐患类别
    Byte cpa;
    Boolean vital;

}



//基本类型Built-in GraphQL::ScalarType有5种 String Int  ID  Boolean Float {精度小数点后5位}。

/*为什么死活不行？ 接收到了请求包数据正常的。为何解析不出来String fNo;这个变量的取值，都是null??。
		"where": {
			"fNo": "dc5kk555ds",
			"cod": "csdfsg3yyy3od"
		},
*/

