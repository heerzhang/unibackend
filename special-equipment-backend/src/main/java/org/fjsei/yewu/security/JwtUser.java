package org.fjsei.yewu.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;


/*
    UserDetails接口 那里实际可简单到就是个字符串就行了，代表用户Spring Security认证信息。
 * Spring Security 验证专用的，和数据库－应用系统Ｕｓｅｒ没关系。
 * SpringBoot 安全 给自己做的Repository,和数据库JPA和应用都无关的。具体应用程序还需要再映射一次User模型对象。
 */

public class JwtUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final Date lastPasswordResetDate;

    public JwtUser(
          Long id,
          String username,
          String firstname,
          String lastname,
          String email,
          String password,
          Collection<? extends GrantedAuthority> authorities,
          boolean enabled,
          Date lastPasswordResetDate
    ) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    //获取当前用户id,未登陆/匿名的是 -1。
    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal=auth.getPrincipal();
        if(principal instanceof JwtUser) {
            Long userid = ((JwtUser) principal).getId();
            return userid;
        }else
             return null;
    }
}


/*
针对Introspection内省功能太过强大，推行强化安全机制措施：
1,直接在JPA实体的getXXX里头限制，比如getAuthorities，缺点是打乱了POJO本来应该的用途，后端其他逻辑获取数据需要额外造个帮助函数来搞。
2，用LAZY异常切断内省查询链条，这条已作废，全局性保持session支持Lazy了； 单向关联关系缺点是该属性字段从代码不能直接利用，必须从反向关系依据id倒着查。
3, 使用graphQL语法interface；可修饰单个查询与单个修改接口函数的输出Type，从而约束前端访问保密字段，缺点是管制力度较弱，还徒增了接口函数个数。
4, 同样使用interface，对外模型定义的字段直接修饰成安全类型，前端能看见的关联模型被限制掉部分属性；想查保密信息需找另搞一个实体字段get函数做，缺点是破坏代码易维护性，前端查保密信息得用特别函数。
5，自定义directive @authr；在*.graphqls文件中规定好某些字段需要更高权限角色才能做内省查询；没注解的走缺省机制{缺省ROLE_USER}。缺点是只能按角色对外模型的过滤可看字段或可调用的函数接口，而不能针对单条数据记录来做细分上的过滤，外模型配置文件可变性很大。
6, @PostAuthorize对实体类不起作用，而对行动类有效,可以直接使用它注解查询与修改各个函数接口权限，在源代码中做了代替配置文件的@authr。缺点是对Introspection管不住，管制力较弱。
7,在*.graphqls模型添加给前端用的新字段(不同名字)，自定义函数里添加各种复杂的过滤条件避免内省信息泄露，被顶替的实体字段可添加角色控制按其他方式去解决,Entity字段要给后端自己内部用。
 这个模式相当于加了一层对前端或者针对第三方局implements部化接口的适配模型层，而原实体是对内的模型层，给各前端程序看的是外模型是经过严格限制权限的适配层模型定义。
8, 针对可能出现的第三方非完全受控系统使用graphQL协议接入后端服务器的场景，考虑从外模式配置文件入手，大幅度简化模型和接口函数数量和限制给该第三方系统的使用权限。
9, 针对特殊函数接口，如auth:，为免去控制introsepction逻辑麻烦，把函数的输出打包改装成普通的JSON字符串/好像绕回去REST那样的接口。
*/
