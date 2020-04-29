package org.fjsei.yewu.resolver.sei;

import com.alibaba.fastjson.JSON;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.fjsei.yewu.entity.sei.*;
import org.fjsei.yewu.entity.sei.inspect.ISP;
import org.fjsei.yewu.entity.sei.inspect.ISPRepository;
import org.fjsei.yewu.entity.sei.inspect.TaskRepository;
import org.fjsei.yewu.entity.sei.oldsys.*;
import org.fjsei.yewu.filter.Person;
import org.fjsei.yewu.filter.SimpleReport;
import org.fjsei.yewu.input.ComplexInput;
import org.fjsei.yewu.input.DeviceCommonInput;
import org.fjsei.yewu.input.WhereTree;
import org.fjsei.yewu.jpa.ModelFiltersImpl;
import org.fjsei.yewu.jpa.PageOffsetFirst;
import md.cm.geography.Address;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.pojo.sei.DeviceSnapshot;
import org.fjsei.yewu.security.JwtUser;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
public class BaseQuery implements GraphQLQueryResolver {

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
    private AddressRepository addressRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EqpMgeRepository eqpMgeRepository;
    @Autowired
    private ElevParaRepository elevParaRepository;
    @Autowired
    private HouseMgeRepository houseMgeRepository;
    @Autowired
    private UntMgeRepository untMgeRepository;
    @Autowired
    private DictEqpTypeRepository dictEqpTypeRepository;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;

    //前端实际不可能用！把数据库全部都同时查入后端内存，太耗；查询只能缩小范围都得分页查，就算原子更新操作也不能全表一个个update，大的表可执行力太差！
    @Deprecated
    public Iterable<EQP> findAllEQPs_留后端自己内部用吧() {
       String partcod="";
       String partoid="";
       Pageable pageable = PageOffsetFirst.of(0, 35, Sort.by(Sort.Direction.ASC,"oid"));         //Integer.parseInt(10)
       Page<EQP> allPage=eQPRepository.findAll(new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(partcod)) {
                    Path<String> p = root.get("cod");
                    predicateList.add(cb.like(p,"%" + partcod + "%"));
                }
                if (!StringUtils.isEmpty(partoid)) {
                    Path<String> p = root.get("oid");
                    predicateList.add(cb.like(p,"%" + partoid + "%"));
                }
                predicateList.add(cb.le(root.get("id"),2100));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        }, pageable);
    /*
    //加了EntityGraph反而更慢HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
             EntityGraph graph =emSei.getEntityGraph("EQP.task");
            //emSei.createQuery("FROM EQP", EQP.class).getResultList()没.setHint(?)没法使用缓存的。
            List<EQP> eqps = emSei.createQuery("FROM EQP", EQP.class)
                    .setHint("javax.persistence.fetchgraph", graph)
              //      .setFirstResult(11)
             //      .setMaxResults(20)
                    .getResultList();
            //getResultStream().limit(10).collect(Collectors.toList()) 已经全表取到内存来了;
    */
       List<EQP>  eqps= allPage.getContent();
       return eqps;        //.subList(74070,74085);
    }

    //多数系统正常地，查询都是直接规定设计好了参数范围的模式，但是灵活性较差，参数个数和逻辑功能较为限制；但安全性好，就是代码上麻烦点。
    public Iterable<EQP> findEQPLike(DeviceCommonInput filter) {
        return eQPRepository.findAll();
    }

    //orderBy 可支持直接指定某属性的下级字段。 {"orderBy": "pos.building",}
    public Iterable<EQP> findAllEQPsFilterInput(DeviceCommonInput filter, int offset, int first, String orderBy, boolean asc) {
        Pageable pageable;

        if(StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc? Sort.Direction.ASC: Sort.Direction.DESC,orderBy));
        Page<EQP> allPage=eQPRepository.findAll(new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(filter.getCod())) {
                 //   Path<Task> p = root.get("task");
                    Path<String> p = root.get("cod");
                    //非集合型的关联对象可以直接指定下一级的字段SingularAttribute。　该属性，Set，List，Map；//最新的一条TASK/ISP,规化成非集合。
                    //PluralAttribute 复数形式Set[];
          //          p = root.get("pos");
               //     Path<String> p2 = p.get("devs");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                   // p = root.get("isps");
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("factoryNo");
                    predicateList.add(cb.like(p,"%" + filter.getOid() + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        }, pageable);

        List<EQP>  eqps= allPage.getContent();
        return eqps;        //.subList(74070,74085)
    }

    public long countAllEQPsFilter(DeviceCommonInput filter) {
        Specification<EQP> spec= new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(filter.getCod())) {
                    Path<String> p = root.get("type");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("factoryNo");
                    predicateList.add(cb.like(p,"%" + filter.getOid() + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        };
        return eQPRepository.count(spec);
    }

    //权限控制?
    public Iterable<Report> findAllReports() {
        return reportRepository.findAll();
    }

    public Iterable<Address> findAllPositions() {
        return addressRepository.findAll();
    }



    //@Transactional    调试模式时会有懒加载的字段数据？
    @Transactional(readOnly = true)
    public Iterable<Unit> findAllUnits() {
        if(!emSei.isJoinedToTransaction())
            emSei.joinTransaction();
        List<Unit>  units=unitRepository.findAll();
        units.stream().forEach(item -> {
            //Set<EQP> eqps=item.getMaints();   懒加载,此时对象该字段还没有数据呢,手动查
           List<EQP> eqps=eQPRepository.findByMaintUnt(item);
           item.setMaints(new HashSet<EQP>(eqps));
           //这里虽然懒加载因为业务需要单位直接关联它维保的设备列表=支持graphQL内省，免去再开独立接口函数去做这工作的复杂模式。
        });
        return units;
    }

    public Iterable<Person> findAllUsers() {
        List<User>  users=userRepository.findAll();
        List<Person>  parents = new ArrayList<Person>();
        parents.addAll(users);
        return  parents;  //这里返回的对象实际还是User派生类型的，只是graphQL将会把它当成接口类型Person使用。
    }
   //Spring Security——基于表达式的权限控制，Spring 表达式语言(Spring EL)；SpEL 操作符; 正则表达式匹配?
   //@PreAuthorize("hasRole('ROLE_'.concat(this.class.simpleName))")
   //SpEL怎样从List、Map集合中取值; @Value("#{numberBean.no == 999 and numberBean.no < 900}")
   // @PreAuthorize("hasRole('ROLE_USER') and hasIpAddress('localhost')" )
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Iterable<Authority> findAllAuthority() {
        //todo: 不能乱用Authority 安全！控制
        return authorityRepository.findAll();
    }

    public Iterable<User> findUserLike(String username) {
        return userRepository.findAllByUsernameLike(username);
    }
    public Iterable<User> findUserLikeInterface(ComplexInput input) {
        return userRepository.findAllByUsernameLike(input.getUsername());
    }
   /*
    public Iterable<Person> findUserLikeInterfacePerson(ComplexInput input) {
      //  return userRepository.findAllByUsernameLike(input.getUsername());
        return null;
    } */
   public Person userBasic(Long id) {
       return userRepository.findById(id).orElse(null);
   }


    public Long countReport(Long ispId) {
        if (ispId == null) return reportRepository.count();
        ISP isp = iSPRepository.findById(ispId).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+isp);
        int myInt=reportRepository.findByIsp(isp).size();
        return Long.parseLong(new String().valueOf(myInt));
    }

    public Long countPositionEQP(Long Id) {
        Address position = addressRepository.findById(Id).orElse(null);
        int myInt=position.getEqps().size();
        return Long.parseLong(new String().valueOf(myInt));
    }
    //验证是否登录，谁登录，有那些角色的。
    public User checkAuth() {
        /*
        获取servlet路径来鉴定权限。 和ROLE_xxx交叉了，又是接口安全域又是角色，多关联关系？复合主码＋? 接口Path+UserName。
        简化做法： 直接从ROLE_字符串区分安全域接口。 ROLE_Outer_xx; ROLE_Main_xx 复合型的权限代码;每个接口默认ROLE_都是唯一性差异来区分。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String servletPath =request.getServletPath();
        */
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)  return null;
        //graphql这块即时没登录也会有系统角色ROLE_ANONYMOUS，奇葩！
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
    //原型对应：getDevice(id:ID!): EQP! 尽量不要返回值必选的，前端会报错，改成getDevice(id:ID!): EQP可选null。
    public EQP getDevice(Long id) {
        return eQPRepository.findById(id).orElse(null);
        //因为LAZY所以必须在这里明摆地把它预先查询出来，否则graphQL内省该字段就没结果=报错; open-in-view也没效果。
        //单层eqp.getTask().stream().count();  //.collect(Collectors.toSet())
        //两层eqp.getTask().stream().forEach(t->t.getIsps().stream().count());
    }

    //前端路由乱来？不是正常的url也来这里了： java.lang.Long` from String "favicon.ico": not a valid Long value
    public EQP getDeviceSelf(Long id) {
        EQP eqp=eQPRepository.findById(id).orElse(null);
        // Assert.isTrue(eqp != null,"未找到EQP:"+id);
        return eqp;
    }

    public Iterable<SimpleReport> getReportOfISP(Long id) {
        ISP isp=iSPRepository.findById(id).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+id);
        return isp.getReps();
    }

    //选择集安全域的 测试：
    //适应安全域考虑，把结果集造型成特定的过滤基类。
    public Iterable<SimpleReport> findAllBaseReports() {
        List<Report>  reps=reportRepository.findAll();
        List<SimpleReport>  parents = new ArrayList<SimpleReport>();
        parents.addAll(reps);
        return  parents;
    }

    public EQP findEQPbyCod(String cod) {
        return eQPRepository.findByCod(cod);
    }
    //支持未登录就能查询角色{}，免去控制introsepction逻辑麻烦，把函数的输出自定义改装成普通的JSON字符串/好像REST那样的接口。
    public String auth() {
        User user=checkAuth();
        if(user==null || !user.getEnabled())  return "{}";        //未登录或者未正常开通使用的就{}
        //只需要很小部分的User内容输出。
        User out=user.cloneAuth();      //user这里不可以直接用JSON.toJSONString(user)，会报错，有些字段LAZY。
        String strJson = JSON.toJSONString(out);
        //todo: {user &&  user.enabled && ' '}没用字段去除，enabled应该都是，否则user为空的。
        //String ret="{\"id\": \"12\", \"username\": \"herzhang\", \"authorities\": [ { \"name\": \"ROLE_USER\"  },{ \"name\": \"ROLE_ADMIN\"  } ], \"dep\": \"709\"}";
        return strJson;
    }

    public Iterable<EQP> getAllEQP() {
        String partcod="L";
        String partoid="1";
        Specification<EQP> spec= new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(partcod)) {
                    Path<String> p = root.get("cod");
                    predicateList.add(cb.like(p,"%" + partcod + "%"));
                }
                if (!StringUtils.isEmpty(partoid)) {
                    Path<String> p = root.get("oid");
                    predicateList.add(cb.like(p,"%" + partoid + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        };
        Sort sort = Sort.by(Sort.Order.asc("oid"), Sort.Order.desc("id"));
        return eQPRepository.findAll(spec, sort);
    }
    public long countAllEQP() {
        String partcod="L";
        String partoid="1";
        Specification<EQP> spec= new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(partcod)) {
                    Path<String> p = root.get("cod");
                    predicateList.add(cb.like(p,"%" + partcod + "%"));
                }
                if (!StringUtils.isEmpty(partoid)) {
                    Path<String> p = root.get("oid");
                    predicateList.add(cb.like(p,"%" + partoid + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        };
        return eQPRepository.count(spec);
    }
    public long countAllEQPsWhere(WhereTree where) {
        Specification<EQP> spec= new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
           /*     if (!StringUtils.isEmpty(filter.getCod())) {
                    Path<String> p = root.get("type");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("factoryNo");
                    predicateList.add(cb.like(p,"%" + filter.getOid() + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                */
                return null;
            }
        };
        return eQPRepository.count(spec);
    }

    public Iterable<User> findAllUserFilter(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        ModelFiltersImpl<User> modelFilters = new ModelFiltersImpl<User>(null);
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                modelFilters.effectCount(2000);    //像回调函数一样。
                return null;
            }
        };

        modelFilters.initialize(specification,emSei);
        modelFilters.effectWhereTree(where);
        Page<User> recipes = userRepository.findAll(modelFilters,pageable);
        return recipes;
    }

    public Report getReport(Long id) {
        Report report=reportRepository.findById(id).orElse(null);
        //若需初始化，snapshot为空的
        if(report!=null && report.getSnapshot()==null){
            String cod=report.getIsp().getDev().getCod();
            DeviceSnapshot dss=new DeviceSnapshot();
            dss.setEqpcod(cod);
            EqpMge eqp=eqpMgeRepository.findByEqpcodEquals(cod);
            ElevPara para=elevParaRepository.getByEqpcodEquals(cod);
            if(eqp==null || para==null)    return report;
            dss.set监察识别码(eqp.getOIDNO());
            dss.set使用证号(eqp.getEQP_USECERT_COD());
            dss.set设备代码(eqp.getEQP_STATION_COD());
            String idcl=eqp.getEQP_SORT();
            DictEqpType dcType=dictEqpTypeRepository.findByIdCodEquals(idcl);
            if(dcType!=null)    dss.set设备类别(dcType.getCLASS_NAME());
            idcl=eqp.getEQP_VART();
            dcType=dictEqpTypeRepository.findByIdCodEquals(idcl);
            if(dcType!=null)    dss.set设备品种(dcType.getCLASS_NAME());
            dss.set型号(eqp.getEQP_MOD());
            dss.set出厂编号(eqp.getFACTORY_COD());
            dss.set单位内部编号(eqp.getEQP_INNER_COD());
            Date  date=eqp.getMAKE_DATE();
            if(date!=null)   dss.set制造日期(date.toString());
            date=eqp.getNEXT_ISP_DATE2();
            if(date!=null)   dss.set下检日期(date.toString());   //下次检验日期2(机电定检，1；
            date=eqp.getALT_DATE();
            if(date!=null)   dss.set改造日期(date.toString());
            dss.set设备使用地点(eqp.getEQP_USE_ADDR());
            Long uid= eqp.getBUILD_ID();
            HouseMge houseMge= uid!=null? houseMgeRepository.findById(uid).orElse(null) :null;
            if(houseMge!=null) {
                dss.set楼盘(houseMge.getBUILD_NAME());
                dss.set楼盘地址(houseMge.getBUILD_ADDR());
            }
            UntMge untMge;
            uid= eqp.getMANT_UNT_ID();
            untMge= uid!=null? untMgeRepository.findById(uid).orElse(null) :null;
            if(untMge!=null)  dss.set维保单位(untMge.getUNT_NAME());
            uid= eqp.getALT_UNT_ID();
            untMge= uid!=null? untMgeRepository.findById(uid).orElse(null) :null;
            if(untMge!=null)  dss.set改造单位(untMge.getUNT_NAME());
            uid= eqp.getMAKE_UNT_ID();
            untMge= uid!=null? untMgeRepository.findById(uid).orElse(null) :null;
            if(untMge!=null)  dss.set制造单位(untMge.getUNT_NAME());
            uid= eqp.getSECUDEPT_ID();
            untMge= uid!=null? untMgeRepository.findById(uid).orElse(null) :null;
            if(untMge!=null) {
                dss.set分支机构(untMge.getUNT_NAME());        //SECUDEPT_ID;     //分支机构ID'
                dss.set分支机构地址(untMge.getUNT_ADDR());
            }
            uid= eqp.getUSE_UNT_ID();
            untMge= uid!=null? untMgeRepository.findById(uid).orElse(null) :null;
            if(untMge!=null) {
                dss.set使用单位(untMge.getUNT_NAME());
                dss.set使用单位地址(untMge.getUNT_ADDR());
            }
            dss.set控制方式(para.getCONTROL_TYPE());
            dss.set电梯层数(para.getELEFLOORNUMBER());
            dss.set电梯站数(para.getELESTADENUMBER());
            dss.set电梯门数(para.getELEDOORNUMBER());
            dss.set运行速度(para.getRUNVELOCITY());
            dss.set额定载荷(para.getRATEDLOAD());
            String strJson = JSON.toJSONString(dss);
            report.setSnapshot(strJson);
        }
        //是否需要重新初始化技术参数设备基本字段呢？
        return report;
    }
    //前端设备列表
    public Iterable<EQP> findAllEQPsFilter(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        ModelFiltersImpl<EQP> modelFilters = new ModelFiltersImpl<EQP>(null);
        Specification<EQP> specification = new Specification<EQP>() {
            @Override
            public Predicate toPredicate(Root<EQP> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                modelFilters.effectCount(2000);    //像回调函数一样。
                return null;
            }
        };

        modelFilters.initialize(specification,emSei);
        modelFilters.effectWhereTree(where);
        /* 这下面对照是这样的： query级缓存时间内，数据库人工修改的，前端靠findAllEQPsFilter查的会滞后才显示。
            注意query-results缓存时间内select from结果集不变，但是列表中某单一个实体的字段却可变动的；人工删除实体会报错。
            @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
            Page<EQP> findAll(@Nullable Specification<EQP> spec, Pageable pageable);
        */
        Page<EQP> list = eQPRepository.findAll(modelFilters,pageable);
        return list;
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


