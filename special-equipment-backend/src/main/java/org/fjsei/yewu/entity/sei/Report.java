package org.fjsei.yewu.entity.sei;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fjsei.yewu.entity.sei.inspect.ISP;
import org.fjsei.yewu.filter.SimpleReport;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

//实体类不可搞 interface： ？可能死循环, 返回结果集只能使用接口/不能做实体转换。

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Report  implements SimpleReport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String type;
    private String  no;

    private Date upLoadDate;
    private String path;
    //单次ISP如果多个报告，每个报告单独打印，单独编制特定编号的报告，单独链接；主报告1+N。
    @ManyToOne
    @JoinColumn
    private ISP isp;   //1个检验可以有很多份子报告，报告类型可以不同的。

    private double  numTest;    //测试表达式

    //测试：高保密性质的2个扩展字段；
    private String  sign;
    private String  detail;
    private String  modeltype;
    private String  modelversion;
    //原始记录内容-JSON；在前端录入和修改的部分。
    //字段长度，数据库要修改定义成 clob
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="CLOB ")
    private String  data;
    //该部分数据-JSON，在编制后提交审核时就能固定化了。可直接复制合并到data，存snapshot仅是接口对接便利的过渡工具。
    //纯粹是后端提供给检验报告的，编制报告的那一时间的相关设备状态数据。接口对接复制完成后就可清空了。
    @Lob
    @Basic(fetch= FetchType.LAZY)
    @Column( columnDefinition="CLOB ")
    private String  snapshot;

    @OneToMany(mappedBy="report" ,fetch = FetchType.LAZY)
    private Set<File> files;

    public  Report(String type,String no,ISP isp){
        this.type=type;
        this.no=no;
        this.isp=isp;
        //初始化
        data="{}";
    }
    //重载是依靠参数类型以及个数和顺序来确定的。
    public  Report(String path,ISP isp,String no){
        this.path=path;
        this.no=no;
        this.isp=isp;
        data="{}";
    }
    //graphql选择集　安全信息控制方式： 单个字段的。
    //@PostAuthorize对实体类不起作用，对行动类有效？    @PostAuthorize("hasRole('ADMIN')") !;
    public Date getUpLoadDate() {
        long rights = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(authority ->
                authority.equals(new SimpleGrantedAuthority("ROLE_cmnAdmin"))
                || authority.equals(new SimpleGrantedAuthority("ROLE_Ma"))  ).count();

        if(rights>0)
            return upLoadDate;
        else
            return null;      //这样就切断了graphQL选择集，前端无法查询该字段也无法嵌套。
        //没有登录的客人也有程序角色："ROLE_ANONYMOUS"；
    }

    //JPA实体从数据库装入的实例化过程，根本就没有运行这个setter()/getter();
    @PreAuthorize("hasRole('ADMIN')")
    public void setUpLoadDate(Date upLoadDate) {

          this.upLoadDate = upLoadDate;
    }
    //内省执行这个函数之前，在graphql/tools/MethodFieldResolver.kt:70里面就已能看到了本条记录的所有相关数据了{含同级的字段}。
    public ISP  isp(Long id){
        return this.isp;
    }
}


