package org.fjsei.yewu.entity.sei.oldsys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_UNT_MGE",
        uniqueConstraints={@UniqueConstraint(columnNames={"UNT_ID"})} )
public class UntMge {
    @Id
    @Column(name = "UNT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;
    private String UNT_NAME;
    private String UNT_ADDR;      //'单位详细地址'  填写很乱，最终门栋地址。
    //个人的 也算=单位？
    private String UNT_ORG_COD;      //组织机构代码
    //居民身份证号就是你的社会信用代码，组织统一社会信用代码  18位+验证码；
    private String UNT_LKMEN;      //单位联系人
    private String UNT_MOBILE;      //单位联系人手机
    private String POST_COD;      //单位邮编
    //UNT_AREA_COD,
    //UNT_LAT,　UNT_LONG, 大多都没有数据的。
}