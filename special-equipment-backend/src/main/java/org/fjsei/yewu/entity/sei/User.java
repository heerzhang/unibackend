package org.fjsei.yewu.entity.sei;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fjsei.yewu.entity.sei.inspect.ISP;
import org.fjsei.yewu.entity.sei.julienne.Following;
import org.fjsei.yewu.filter.Person;
import org.fjsei.yewu.security.JwtUser;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//JPA实体类上加组合唯一索引https://blog.csdn.net/qq_29663071/article/details/80092538
//implements是graphQL-java套JPA用；服务端按照接口interface的字段,自动从Entity提取返回给协议执行器。
//User和其接口类Person按照名字一个对一个，这些*.graphql配置文件内不支持交叉使用,还要和JPA部分代码也保持一致。
//?普通的Java类并不在spring管理下，不能使用spring注入的service类。

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USERS",
        uniqueConstraints={@UniqueConstraint(columnNames={"username"})} )
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "")
public class User implements Person {
    //注意id可能带来麻烦，数据库重整，可seq却从小开始，报唯一性约束错！select user_seq.nextval from dual;
    //旧表可修改initialValue到旧的表最大ID值，ID最多64位，就是19个数字的字符串，相当于说是无限大的。
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", initialValue = 1, allocationSize = 1, sequenceName = "user_seq")
    private Long id;

    //这里的@Size注解：1个中文字符也算1作个char的。
    @Column(name = "USERNAME",  unique = true)
    @NotNull
    @Size(min = 2, max = 30)
    private String username;

    @Size(min = 6, max = 50)
    private String password;

    @Size(min = 1, max = 20)
    private String firstname;

    @Size(min = 1, max = 20)
    private String lastname;

    @Size(min = 6, max = 70)
    private String email;

    //该用户是合法的？ 未审核/屏蔽用户。前端控制用于页面的。
    @Column(name = "ENABLED")
    @NotNull
    private Boolean enabled=true;

    //这个字段代表密码更新，token该失效了。本字段不能比Now()超前。
    @Column(name = "LASTPASSWORDRESETDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordResetDate;

    //必须为账户都设置权限，否则graphQL这层就无法访问后端数据。
    //这里维护多对多关系(JPA自动生成一个中间维护联系的关联表)，对方的实体仅仅说明我方的字段名。
    //设置小心，故障：hibernate.LazyInitializationException: failed to lazily initialize;
    //分页查询显示的，FetchType.EAGER 实际比FetchType.LAZY 也慢不了多少的，EAGER导致fetch join的一次查询结果集的行数量由于集合笛卡尔积很可能暴涨，两个集合关联性等因素影响；
    //一般graphQL遇FetchType.EAGER也会利用left outer join。就像是@EntityGraph它也会搞的fetch join;　
    //用.EAGER只要关联查User就必然多出1个sql;若只是查User.id采用.EAGER就不会多出sql;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "")
    private Set<Authority> authorities;
    //上面Set若是换成List就会导致启动失败hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags
    //上面Lazy导致前端无法登陆。

    private String dep;
    private String mobile;

    @OneToMany(mappedBy = "checkMen")
    private Set<ISP> checks;
    //暂时应对：hibernate.LazyInitializationException: failed to lazily initialize XXXXX could not initialize proxy - no Session错误
    //把fetch= FetchType.LAZY,改成fetch= FetchType.LAZY,牺牲性能；GraphQL经常会关联的多层嵌套查询，前端决定的查询关系，不预先提取数据，运行报错。
    //不要fetch= FetchType.EAGER,User对象每次请求都要读取的，isp数据多。
    //为何要维护这个关联表？不见得必须的,多层关联还是直接联系，关联数据的逻辑也得维护。
    @ManyToMany(mappedBy="ispMen")
    private Set<ISP> isp= new HashSet<>();

    //头像
    private String  photoURL;

    @OneToMany(mappedBy = "fromUser")
    private Set<Following>  iFollowing;

    @OneToMany(mappedBy = "toUser")
    private Set<Following>  beFollowed;
    //旧平台的
    private String  旧账户;
    //外部认证；
    private String  authType;
    private String  authName;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /*这里堆栈:　在这个AsyncExecutionStrategy的这里public CompletableFuture<ExecutionResult> execute底下；
    graphql.execution.ExecutionStrategy在ExecutionStrategy文件当中的CompletableFuture<FieldValueInfo> resolveFieldWithInfo
     */
    //安全信息控制方式： 单个字段的。
    public Set<Authority> getAuthorities() {
        long rights = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(authority ->
                authority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ).count();
        if(rights>0)    //超级用户可以看所有人权限表
            return authorities;
        else {
            //当前SpringSecurity验证给出的用户Principal映射出来的User实体表ID。
            Long curruser= JwtUser.getUserId();
            if(id!=curruser)    //非超级用户，就不要看他人的权限列表
                 return null;      //这样就切断了graphQL选择集，前端无法查询该字段也无法嵌套。
            else
                return authorities;
        }
    }
    //另外只给后端自身使用的替代函数：
    //本函数特殊！！
    public Set<Authority> heHasRoles() {
        return authorities;
    }
    //用不到?
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public  User(String name,String dep){
        this.username=name;
        this.dep=dep;
        this.lastPasswordResetDate =new Date();
        this.enabled=false;
    }

    //Entity这里的函数优先级比resolver要低？
    //对应同名字外模型User的约定enabled字段,graphQL都会来这里的,没带参数就是=null缺省;字段就如同函数那样。
    public Boolean getEnabled(Boolean isUsing) {
        //return  this.enabled==isUsing;
        return  this.enabled;
    }
    //Value Resolution/值解析ResolveFieldValue; resolver为objectType提供的内部函数，用以决定名为fieldName的字段的解析值。
    @PreAuthorize("hasRole('ADMIN')")
    public String dep(String dep){
        //如果前端就不给参数，那么这个输入参数是null
        if(dep!=null && !dep.equals(this.dep))      //不是修饰字段参数同样的预定的部门
            return null;
        return this.dep;
    }
    //给auth接口前端返回定制的JSON信息
    @PreAuthorize("hasRole('ADMIN')")
    public User cloneAuth(){
        User auth= new User(this.username,this.dep);
        auth.setId(this.id);
        //auth.setEnabled(this.enabled);
        Set<Authority> outAuthorities=new HashSet<Authority>();
        this.authorities.stream().forEach(authority -> {
                    Authority  outAuthority = new Authority();
                    outAuthority.setName(authority.getName());
                    outAuthorities.add(outAuthority);
                } );
        //Todo: 不同安全域需要的ROLE_XxYyy也不一样啊;　ROLE_cmnXxx通用的部分。
        auth.setAuthorities(outAuthorities);
        return auth;
    }
}



/*
JPA 如何指定底层数据库的存储空间文件，分区文件。
@Column：(secondaryTable：如果此列不建在主表上（默认是主表），该属性定义该列所在从表的名字)
        主要用在主表，子表是自行定义，映射时使用两个类（集成关系），但为一个实体，保存到两个表的情况
@SecondaryTable(name = "xx", pkJoinColumns = @PrimaryKeyJoinColumn(name = "xid"))
　           ? ）extends从表名。
@Table属性：( catalog 和 sechema 属性指定据库名=一般不需要){} 像个域名create table door.newsei.EQP {}
*/

//用哪个好？　@Column(size = 50)或@Size(max = 50)   https://www.cnblogs.com/ealenxie/p/10938371.html
/*修改数据源NativeQuery, 配置schema; {h-schema}占位符Hibernate语法；不用建同义词synonym
    @Query("select * from {h-schema}user", nativeQuery=true)
 spring.jpa.properties.hibernate.default_schema=my_schema
*/
