package md.specialEqp;

//枚举_Enum取值 的数字/字符串不要轻易修改，数据库/ES存储直接相关的。
//枚举ORDINAL 数字是从0开始的，0,1,2,3...ordinal数字无法手动设置！只能顺序来。
//对应前端的Enum显示选择列表，<Select /><option value={'USE'}>在用</option> value就是USE等name,展示层文本"在用"是desc中文扩展;
//扩展desc中文描述：实际上没啥意义了。

/**EQP_USE_STA=[{id:'1',text:'未投入使用'},{id:'2',text:'在用'}, {id:'3',text:'停用'},
 {id:'4',text:'报废'},{id:'5',text:'拆除'},{id:'6',text:'迁出'},{id:'7',text:'垃圾数据'}
 ,{id:'8',text:'删除(移除监察)'},{id:'9',text:'在用未注册'}];
 * */

public enum UseState_Enum {
    _0,
    /**未投入使用*/
    NOTINUSE,
    /**在用*/
    USE,
    /**停用*/
    STOP,
    /**报废*/
    DISCARD,
    /**拆除*/
    DEMOLISH,
    /**迁出*/
    MOVEOUT,
    /**垃圾数据*/
    DELETE,
    /**删除(移除监察)*/
    REMOVESUPV,
    /**在用未注册*/
    USENOTREG;


    /*
       USE( "在用"),
       STOP( "停用"),
    中文描述；该字段不会存储到ES的/DB,只在内存有效。
     * java IDE可显示, 只在服务端有用，接口前端用不上。
     * graphQL:Sub selection not allowed on leaf type UseState_Enum! of field ust
    private String desc;
    private UseState_Enum(String desc){
        this.desc=desc;
        //this.ordinal不可设置的。
    }
    public String getDesc(){
        return desc;
    }
    public static void main(String[] args) {
        for (UseState_Enum day : UseState_Enum.values()) {
            System.out.println("name:" + day.name() +
                    ",desc:" + day.getDesc());
        }
    }
    */
}


//_Enum的内部 列表名字若被修改，ES查询会报错，需更新数据。ES存储区分大小写的字符串形式name;ES不存储数字序号。
//自己扩展desc 中文描述， 该字段不会存储到ES的/DB,只在内存有效。