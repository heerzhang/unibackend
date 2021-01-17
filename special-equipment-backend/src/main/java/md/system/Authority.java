package md.system;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

//实际AuthorityName才是配置点，Authority表却是还没能CUD生成修改的记录。

@Entity
@Table(name = "AUTHORITY" )
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Fast")
public class Authority {

    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq")
    //@SequenceGenerator(name = "authority_seq", sequenceName = "authority_seq", allocationSize = 1)
    private Long id;

    /**直接上字符串enum保险点,用数字还要再映射,可惜用不上中文*/
    @Column(name = "NAME", length = 40)
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

    //设置小心，故障：hibernate.LazyInitializationException: failed to lazily initialize
    //FetchType.LAZY，产生查询异常失败；有意外好处，切断graphQL的关联查询嵌套，避免信息安全问题。
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
    private List<User> users= new LinkedList<>();
    //private Set<Isp> isp= new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
