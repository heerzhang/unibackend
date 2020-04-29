package org.fjsei.yewu.resolver.sei.julienne;

import graphql.kickstart.tools.GraphQLQueryResolver;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.TaskRepository;
import md.julienne.Following;
import md.julienne.Recipe;
import md.julienne.RecipeRepository;
import org.fjsei.yewu.input.WhereTree;
import org.fjsei.yewu.jpa.ModelFiltersImpl;
import org.fjsei.yewu.jpa.PageOffsetFirst;
import org.fjsei.yewu.security.JwtUser;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


//import org.springframework.data.jpa.repository.EntityGraph;   简名同名字冲突
//@Transactional类完全限定名：而不是javax.的那一个。
//import javax.transaction.Transactional;
//import java.sql.Date;


//实际相当于controller;
//这个类名字不能重复简明！
//graphQL安全性(query/mutation返回的对象可以进行id关联嵌套查询，如何控制关联信息访问)，这方面apollo做的较好：@注释扩展。
//信息安全私密字段不建议用graphQL的关联嵌套=内省查询，独立配合用REST接口也是候选方式。
//这里接口函数比graphqls模型多出了也没关系。

@Component
//@Transactional(readOnly = true)  ?查询60秒就超时。
public class JulienneQuery implements GraphQLQueryResolver {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EQPRepository eQPRepository;
    @Autowired
    private ISPRepository iSPRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;

    //验证是否登录，谁登录，有那些角色的；
    //不暴露给外部graphql接口模型，仅供内部使用。
    /*graphql接口文件里面定义的 extend type Query {
            checkAuth: User
      }
     不支持多个GraphQLResolver类的方法名字都是同名的checkAuth，必须不一样的名称 =唯一性。
    */
    private User checkAuth() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)  return null;
        Object principal=auth.getPrincipal();
        if(principal instanceof JwtUser){
            Long  userid=((JwtUser) principal).getId();
            //   UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(name);
            //Long userId=Long.valueOf(12);
            return userRepository.findById(userid).orElse(null);
        }else{
            //没有登录的也Authenticated! 有anonymousUser 有ROLE_ANONYMOUS；
            return null;
        }
    }
    //返回relation.fromUser+toUser+confirmed内容
    public Iterable<Following> useFollowing(boolean toUser) {
        User user= checkAuth();
        if(user==null)   return null;
        /* List<User>  parents = new ArrayList<User>();
        if(toUser)  user.getBeFollowed().stream().forEach(following -> {
            parents.add(following.getFromUser());
        });
        else user.getIFollowing().stream().forEach(following -> {
            parents.add(following.getToUser());
        });
        return  parents;   */
        //toUser=true被人关注;
        if(toUser)
            return user.getBeFollowed();
        else
            return user.getIFollowing();
    }
    //某个菜谱：查询的权限控制，关注后的人，建立者才可以看：
    public Recipe findRecipe(Long id) {
        User user= checkAuth();
        if(user==null)   return null;
        return recipeRepository.findById(id).orElse(null);
    }
    //过滤排序分页，查询所有的某一个 创建者的菜谱；　前端送来的一次查询就要一次的权限检查；
    public Iterable<Recipe> findAllRecipeFilter(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些　创建者的菜谱。
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy)) {
            pageable = PageOffsetFirst.of(offset, first, Sort.by(Sort.Order.desc("id")));
        }
        else {
            //防止分页查询的 等值排序引起重复现象，需要添加唯一性排序字段。 缺省的ID是降序的，无需要客户端再次指定id排序。
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Order.asc(orderBy) : Sort.Order.desc(orderBy), Sort.Order.desc("id")));
        }
        //用final修饰引用类型变量p时，不能对p进行重新赋值，可以改变p里面属性的值；
        ModelFiltersImpl<Recipe> modelFilters = new ModelFiltersImpl<Recipe>(null);

        Specification<Recipe> specification = new Specification<Recipe>() {
            //这里反而放在 .effectWhereTree(where) 之后才执行的。
            @Override
            public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                //Predicate predicate = cb.conjunction();
                //List<Expression<Boolean>> expressions = predicate.getExpressions();
                modelFilters.effectCount(2000);    //像回调函数一样。
                //Set<Root<?>> roots3 = query.getRoots();       //Union++ 才会有多个顶级的Root；
                //return predicate;

                return null;
            }
        };

        modelFilters.initialize(specification,emSei);
        modelFilters.effectWhereTree(where);
        /*接口类 List<T> findAll(@Nullable Specification<T> spec);
	            Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable);
        */
        //Page有暴露Iterable接口:
        Page<Recipe> recipes = recipeRepository.findAll(modelFilters,pageable);
        return recipes;
    }
}



/*执行策略ExecutionStrategy都会导致lazy失败，这里读取已跨越DB的session延迟异步读取啊，
搞成了Entity和GraphQLResolver<TYPE>两个类独立分离了，还一样Lazy懒加载错误。
像@OneToMany这类JPA注解关联关系：对方类型必须是实体的。Object对象类型不能是接口interface的。
@MappedSuperclass注解的使用 Entity类之间的继承关系    https://blog.csdn.net/zty1317313805/article/details/80524900
使用懒加载时，报异常：session失效https://blog.csdn.net/wanping321/article/details/79532918
EntityManager不会立刻关闭，导致连接池连接数占用。高并发的系统最好不要使用OpenEntityManagerInView模式；https://blog.csdn.net/q1054261752/article/details/54773428
Spring动态替换Bean 接口=BeanPostProcessor； https://www.jianshu.com/p/853a081e4a02
若toPredicate内部应用：Subquery无法一次性选择多个字段出来做表达式比较条件!!，附加新建一个CriteriaQuery却又无法连接原始query报错无法serialize;
JPA关联嵌套子查询correlate subquery；而From()是不支持子查询的。 非关联子查询指子查询可以脱离主查询独立执行；关联子查询限制是子查询不能返回多于1行的数据.
*/


