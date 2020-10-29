package org.fjsei.yewu.resolver.sei.inspect;

import graphql.kickstart.tools.GraphQLQueryResolver;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISP;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.Task;
import md.specialEqp.inspect.TaskRepository;
import org.fjsei.yewu.input.WhereTree;
import org.fjsei.yewu.jpa.ModelFiltersImpl;
import org.fjsei.yewu.jpa.PageOffsetFirst;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.hibernate.Metamodel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;

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
public class IspMgrQuery implements GraphQLQueryResolver {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EqpRepository eQPRepository;
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
    private AddressRepository addressRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;


    public Iterable<ISP> findAllISPs() {
        return iSPRepository.findAll();
    }

    public Iterable<Task> findAllTasks() {
        return taskRepository.findAll();
    }


    public Long countISP(Long userId) {
        if (userId == null) return iSPRepository.count();
        User ispmen = userRepository.findById(userId).orElse(null);
        Assert.isTrue(ispmen != null,"未找到ispmen:"+ispmen);
        int myInt=iSPRepository.findByIspMen(ispmen).size();
        return Long.parseLong(new String().valueOf(myInt));
    }
    //AccessDeniedException: 不允许访问; @PreAuthorize("hasRole('ROLE_cmn'.concat(this.class.simpleName))")
    public Long countTask(String dep, String status) {
       // HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       // String servletPath =request.getServletPath();
        int result;
        if (StringUtils.isEmpty(dep) && StringUtils.isEmpty(status))
            result=(int)taskRepository.count();
        else if (StringUtils.isEmpty(dep))
            result=taskRepository.findByStatus(status).size();
        else if (StringUtils.isEmpty(status))
            result=taskRepository.findByDep(dep).size();
        else
            result=taskRepository.findByDepAndStatus(dep,status).size();
        //这样方式，findByDepAndStatus 遇到参数为空的，会把null参数也直接当成了AND条件之一，必然查询结果很少了。AND :a is null
        return Long.parseLong(new String().valueOf(result));
    }

    public ISP getISP(Long id) {
        return iSPRepository.findById(id).orElse(null);
    }


    //如何应对复杂的过滤和关联表查询条件呢？分页排序；其实apollo分页有3种模式offset,cursor,edges的。
    //？filter考虑升级成interface=多个参数的过滤？模式多参数聚合对象 AND OR；？先考虑简单的，offset模式。
    //Long id=最最简单的filter；      AND OR；必然在页面已经考虑和选择完毕的。
    //关联附属对象的字段来做过滤， 针对宿主对象模型， 关联能几多个？ 几层嵌套层次限制=2层最多了；。。。
    //分页针对宿主模型做的，内省的关联嵌套的，不分页但可以限制条数和深度，内省的更难控制输出记录数？内省的也排序啊。
    public Iterable<ISP> isp(Long id, int offset, int first, String orderBy) {
        List<ISP>   isplist=iSPRepository.getByDev_IdOrderById(id);
        if(offset>=isplist.size())  return null;
        if(offset<0)   offset=0;
        if(first<=0 || first>1000)  first=20;
        int toindex=offset+first;
        if(toindex>=isplist.size())     toindex=isplist.size();
        return   isplist.subList(offset, toindex);  //不含toindex的
    }

    //设计过滤复杂条件;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Iterable<ISP> findAllISPfilter_2samples(WhereTree where, int offset, int first, String orderBy, boolean asc) {
       //多个orderBy[orderBy  ? ?]
        Pageable pageable;
        if(StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc? Sort.Direction.ASC: Sort.Direction.DESC,orderBy));

        //用final修饰引用类型变量p时，不能对p进行重新赋值，可以改变p里面属性的值；
        ModelFiltersImpl<ISP> modelFilters=new ModelFiltersImpl<ISP>(null);

        Specification<ISP> specification=  new Specification<ISP>() {
                //这里反而放在 .effectWhereTree(where) 之后才执行的。
                @Override
                public Predicate toPredicate(Root<ISP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    query.distinct(true);
                    Predicate predicate = cb.conjunction();
                    List<Expression<Boolean>> expressions = predicate.getExpressions();
                    modelFilters.effectCount(2000);    //像回调函数一样。
                   Set  joins= root.getJoins();
                   //属性字段和查询语句 嵌套深度有关。 主查询是.querys.get(0). ; 子查询1,2,..
                    Join<?, ?> join1 =modelFilters.querys.get(0).prepareJoin("ispMen");
               //     prepareJoin(String zdName);

                 //   Join<?, ?> join1 = root.join("ispMen" ,JoinType.LEFT);
                    Join<?, ?> joins0 =modelFilters.querys.get(0).prepareJoin("task");

     /*               joins0.getAttribute().getName();

                    if(joins0.getAttribute().getDeclaringType().getJavaType() == ISP.class)
                        nma1="得住";
        */
                    expressions.add(cb.or( cb.isNull(root.get("task") ) ,
                            cb.equal(joins0.get("status"), "started" )
                            )
                    );
                    Set<Root<?>> roots3=query.getRoots();       //Union++ 才会有多个顶级的Root；
                    return predicate;
                }
            };

        modelFilters.initialize(specification,emSei);
        modelFilters.effectWhereTree(where);
  /*      List<ISP> allPage2=iSPRepository.findAll(modelFilters);
        if(allPage2.size()>=0)
            return allPage2;
    */
        Page<ISP> allPage=iSPRepository.findAll(new Specification<ISP>() {
            @Override
            public Predicate toPredicate(Root<ISP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                String  classname1="User";
                query.distinct(true);
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();

                //ManyToMany集合ispmen比较特别内部自动做2次join关联一个中间表；
                Join<?, ?> join1 = root.join("ispMen" , JoinType.LEFT);       //ManyToMany的我方属性名;
/*
    Subquery<?>  subquery = query.subquery( root.getModel().getSet("reps").getElementType().getJavaType() );
    Root<?> subRoot = subquery.correlate(root);
    Join<?, ?>  subEntity = subRoot.join("reps");
    subquery.select(subEntity.get("id"));
    Predicate p = cb.conjunction();
    List<Expression<Boolean>>  expressions2 = p.getExpressions();
    //OneToMnay字段必须得到isp亦即(mappedBy ="isp")=对方属性名。
    //expressions2.add( cb.equal(subEntity.get("isp"), root) );     //Root和Path都是Expression可直接比较
    expressions2.add( cb.equal(subEntity.get("numTest"), "2") );
    subquery.where(p);
    //子查询2end
    expressions.add(cb.or(  cb.not( cb.exists(subquery) ) ));
   */
                //子查询1begin     correlate三种root,Setjoin,join;
           /*         //若是：correlate从root开始begin     这个方式结果是isp0_.id关联的子查询select。
                    Subquery<?>  subquery = query.subquery(join1.getJavaType());
                    //Root<?>  subRoot = subquery.from(join1.getJavaType());       //=User.class;
                    Root<?> subRoot = subquery.correlate(root);
                    Join<?, ?>  subEntity = subRoot.join("ispMen");
                    subquery.select(subEntity.get("id"));
                    //Join<?, ?>  subEntity = join1.join(subRoot);    //ispmen.User[]
                    //subquery.select(subEntity.get("id"));
                    Predicate p = cb.conjunction();
                    List<Expression<Boolean>>  expressions2 = p.getExpressions();
                    //ManyToMany的集合特殊，必须jion中间表; OneToMany不需要。
            //        expressions2.add( cb.equal(subRoot.get("id"), join1) );     //Root和Path都是Expression可直接比较
                    //该ISP.ispMen集合中不存在这样的User;
                    expressions2.add(  cb.and( cb.equal(subEntity.get("username"), "herzhang" ),
                            cb.equal(subEntity.get("dep"), "15" ))
                     );
                    //若是：correlate从root开始end    */
                //若是：correlate从Setjoin+join1@"ispMen"开始begin       这个方式结果是user2_.id关联的子查询select;级联到ispmen了。
                Class<?> subType=join1.getJavaType();       //这个是sei.User类型!

               // javax.persistence.metamodel.Metamodel  metamodel22= emSei.getMetamodel();
                Metamodel metamodel=(Metamodel)emSei.getMetamodel();
               // String  longName=metamodel.getImportedClassName("ISP");
                EntityType<?> entityType= metamodel.entity(metamodel.getImportedClassName("ISP"));
                Class  subClass=null;
                subClass =entityType.getJavaType();

                Subquery<?> subquery = query.subquery(subClass);
                //非关联添加的
                Root<?> subRoot = subquery.from(subClass);
                From<?, ?> subEntity=subRoot;
         //       Join<?, ?>  subRoot = subquery.correlate(join1);        //关联必须从底下连接处找起的，相对定位；子语句属性名如何分别？
                //关联子查询没有用from()的，使用correlate()，需要从关联字段的基础上再去向着目标from模型类join;
                //关联子查询query.subquery()的模型类型是关联字段的模型类，注意它不是目标的from模型类；
          //      Join<?, ?>  subEntity = subRoot.join("checks"); //底下端那个字段
         //       Bindable<?> bindable= subEntity.getModel();     //这个是SET类型name="checks",这里declaringType=User;
                //ISP==>""ispMen"其中一个人" user2_.id="USER."checks 他做的所有检验"""ISP"""   "

                subquery.select(subEntity.get("id"));
                Predicate p = cb.conjunction();
                List<Expression<Boolean>>  expressions2 = p.getExpressions();
                //非关联添加的        ispMen.#1 代表上１级的SQL内的属性;
                expressions2.add(  cb.and( cb.equal(subEntity.get("checkMen"), join1) )
                );          //checkMen_id    User checkMen;
                expressions2.add(  cb.and( cb.equal(subEntity.get("conclusion"), "haha" ),
                        cb.equal(subEntity.get("nextIspDate"), java.sql.Date.valueOf("2019-03-02")  ))
                );
                //若是：correlate从Setjoin+join1@"ispMen"开始end
                subquery.where(p);
                //子查询1end
                 //cb.or( cb.isNotEmpty(root.get("ispMen") )
                //    , cb.not( cb.exists(subquery) )
                expressions.add(cb.or(  cb.not( cb.exists(subquery) ) ));

                        //          expressions.add(cb.or( cb.diff(new Long(((Date)root.get("nextIspDate")).getTime()) ,(Long) ((Date)cb.currentDate()).getTime()   )
                /*日期求取相差天数; diff最少一个数值型的。
                expressions.add(cb.or( cb.gt(
                        cb.sum( ( Expression ) root.get("nextIspDate") , 22.89 )
                        ,  ( Expression )cb.currentDate()
                        )
                ) ); */
          /*      expressions.add(cb.or( cb.equal(
                        ( Expression ) cb.substring(root.get("nextIspDate"),1,4)
                        ,  "2018"
                        ), cb.ge( cb.size(root.get("ispMen")), 2)
                ) );
           */
                //访问下级属性字段才需要join;
                Join<?, ?> joins0 = root.join("task" , JoinType.LEFT);
                expressions.add(cb.or( cb.isNull(root.get("task") ) ,
                               cb.equal(joins0.get("status"), "started" )
                        )
                );


                Join<?,?> join2 =  join1.join("checks", JoinType.LEFT);
                //expressions.add( cb.isTrue( cb.equal(join2.get("conclusion"), "haha" )  ) );
                        //cb.or( cb.isEmpty(join1.get("checks") ),
                expressions.add(cb.or( cb.gt(cb.size(join1.get("checks")),12 ),
                        cb.gt( cb.length(join2.get("conclusion")), 3 )
                        )
                );

   //             Join<?, ?> join3 = root.join("reps" ,JoinType.LEFT);      //访问集合属性下一级字段才用join;否则可省略。


            //子查询2begin
               // Subquery<?>  subquery = query.subquery( root.getModel().getSet("reps").getElementType().getJavaType() );       //暂时<set>.<set>
            /*
                Subquery<?>  subquery = query.subquery( root.getModel().getSet("reps").getElementType().getJavaType() );
                Root<?> subRoot = subquery.correlate(root);
                Join<?, ?>  subEntity = subRoot.join("reps");
                subquery.select(subEntity.get("id"));
                Predicate p = cb.conjunction();
                List<Expression<Boolean>>  expressions2 = p.getExpressions();
                //OneToMnay字段必须得到isp亦即(mappedBy ="isp")=对方属性名。
                //expressions2.add( cb.equal(subEntity.get("isp"), root) );     //Root和Path都是Expression可直接比较
                expressions2.add( cb.equal(subEntity.get("numTest"), "2") );
                subquery.where(p);
            //子查询2end
                expressions.add(cb.or(  cb.not( cb.exists(subquery) ) ));
            */
      /**          //子查询3begin
                Subquery<?>  subquery = query.subquery( root.getModel().getSet("reps").getElementType().getJavaType() );       //暂时<set>.<set>
                Root<?> subRoot = subquery.from( root.getModel().getSet("reps").getElementType().getJavaType() );
               //subquery.select( (Expression) cb.<Long>greatest(subRoot.get("numTest")));
                //greatest/least()需要指定类型，max()不需要设置类型;
                subquery.select( ( Expression ) cb.max(subRoot.get("numTest")));
                Predicate p = cb.conjunction();
                List<Expression<Boolean>>  expressions2 = p.getExpressions();
                //OneToMnay字段必须得到isp亦即(mappedBy ="isp")=对方属性名。
                expressions2.add( cb.equal(subRoot.get("isp"), root) );     //Root和Path都是Expression可直接比较
      //          expressions2.add( cb.greaterThan( cb.<Date>greatest(subRoot.get("upLoadDate")), java.sql.Date.valueOf("2005-12-12")) );
                subquery.where(p);
                //子查询3end
                //Tuple    https://blog.csdn.net/stronglyh/article/details/49887941
 //               TypedQuery<Tuple> q = entityManager.createQuery(query);
                //集合大小？ cb.ge( cb.size(root.get("reps")), 4 )
                //cb.ge( cb.size(root.get("reps")), 1 )
                //    cb.not(cb.exists(subquery))
                //   cb.greaterThan(subquery.getSelection() , java.sql.Date.valueOf("2005-12-12") )       //不存在这样的？简单逻辑无法搞定!
                expressions.add( cb.<Double>greaterThan( (Expression<Double>) subquery.<Double>getSelection() , new Double(22.3) ) );
        **/

                /*/子查询５begin  //无需明确指出对端是哪个字段isp来映射我方的这个字段reps关系。
                Subquery<?>  subquery = query.subquery( root.getModel().getSet("reps").getElementType().getJavaType() );
                Root<?> subRoot = subquery.correlate(root);
                Join<?, ?>  subEntity = subRoot.join("reps");       //一对多的;
                subquery.select( ( Expression ) cb.max(subEntity.get("numTest") ) );        //.select(cb.literal(1) )
        //           Predicate p = cb.conjunction();
                //List<Expression<Boolean>>  expressions2 = p.getExpressions();
        //            subquery.where(p);
                expressions.add( cb.<Double>greaterThan( (Expression<Double>) subquery.<Double>getSelection() , new Double(22.3) ) );
                //子查询５end   */

                Set<Root<?>> roots3=query.getRoots();       //Union++ 才会有多个顶级的Root；
              /*  Set<Join<?, ?>>  set= subquery.getCorrelatedJoins();
                Set<?>   joins3=  subRoot.getJoins();
                From<?, ?> from6= ((Join<?, ?>)(joins3.toArray()[0])).getParent();
                From<?, ?> from7= from6.getCorrelationParent();
                Set<Root<?>> roots5=subquery.getRoots(); */
                return predicate;
            }
        }, pageable);

        List<ISP>  eqps= allPage.getContent();
        return eqps;
    }

    public Iterable<ISP> findAllISPfilter(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        //多个orderBy[orderBy  ? ?]
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        //用final修饰引用类型变量p时，不能对p进行重新赋值，可以改变p里面属性的值；
        ModelFiltersImpl<ISP> modelFilters = new ModelFiltersImpl<ISP>(null);

        Specification<ISP> specification = new Specification<ISP>() {
            //这里反而放在 .effectWhereTree(where) 之后才执行的。
            @Override
            public Predicate toPredicate(Root<ISP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
        List<ISP> allPage2 = iSPRepository.findAll(modelFilters);
        return allPage2;
    }

    public Iterable<Task> findAllTaskFilter(WhereTree where, int offset, int first, String orderBy, boolean asc) {
       // User  user= checkAuth();
      //  if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        ModelFiltersImpl<Task> modelFilters = new ModelFiltersImpl<Task>(null);
        Specification<Task> specification = new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                modelFilters.effectCount(2000);    //像回调函数一样。
                return null;
            }
        };

        modelFilters.initialize(specification,emSei);
        modelFilters.effectWhereTree(where);
        Page<Task> items= taskRepository.findAll(modelFilters,pageable);
        return items;
    }
    //Todo： 排除多条ISP对应一个[Task+Dev]组合的可能性。
    //最多1条正常状态的ISP， 唯一性保证： device/157/task/184 ;未派工的null
    public ISP getISPofDevTask(Long dev, Long task) {
        List<ISP> allPage =iSPRepository.getByDev_IdAndTask_IdOrderByNextIspDate(dev,task);
        if(allPage.size()==0)     return null;
        else return allPage.get(0);
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


