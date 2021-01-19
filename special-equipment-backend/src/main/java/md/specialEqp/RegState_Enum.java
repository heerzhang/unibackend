package md.specialEqp;
//先有旧的数据，才再去搞的enum定义，数据库DB存储用Byte 1个字节/数字

/**注册状态
 var EQP_REG_STA=[{id:'0',text:'待注册'},{id:'1',text:'在册'},{id:'3',text:'注销登记'}];
 * EQP_REG_STA=2啥？有2条。
 */

public enum RegState_Enum {
    /**待注册*/
    TODOREG,
    /**在册*/
    REG,
    /**?*/
    _2,
    /**注销登记*/
    CANCEL;
}

