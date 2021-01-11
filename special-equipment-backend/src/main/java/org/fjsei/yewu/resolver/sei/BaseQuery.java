package org.fjsei.yewu.resolver.sei;

import com.alibaba.fastjson.JSON;
import com.querydsl.core.BooleanBuilder;
import graphql.kickstart.tools.GraphQLQueryResolver;
import md.specialEqp.inspect.IspRepository;
import md.specialEqp.type.ElevatorRepository;
import md.system.*;
import md.cm.unit.Unit;
import md.cm.unit.UnitRepository;
import md.computer.FileRepository;
import md.specialEqp.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.fjsei.yewu.entity.fjtj.*;
import md.specialEqp.inspect.Isp;
import md.specialEqp.inspect.TaskRepository;
import md.specialEqp.Equipment;
import org.fjsei.yewu.filter.SimpleReport;
import org.fjsei.yewu.filter.UserBase;
import org.fjsei.yewu.index.sei.*;
import org.fjsei.yewu.input.ComplexInput;
import org.fjsei.yewu.input.DeviceCommonInput;
import org.fjsei.yewu.input.UnitCommonInput;
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
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.*;


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
    private EqpRepository eQPRepository;
    @Autowired
    private EqpEsRepository eqpEsRepository;
    @Autowired   private CompanyEsRepository companyEsRepository;
    @Autowired private PersonEsRepository personEsRepository;
    @Autowired
    private ElevatorRepository elevatorRepository;
    @Autowired
    private IspRepository iSPRepository;
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
    @Autowired
    private ElasticsearchRestTemplate  esTemplate;

    //前端实际不可能用！把数据库全部都同时查入后端内存，太耗；查询只能缩小范围都得分页查，就算原子更新操作也不能全表一个个update，大的表可执行力太差！
    @Deprecated
    public Iterable<Eqp> findAllEQPs_删除() {
       String partcod="";
       String partoid="";
       Pageable pageable = PageOffsetFirst.of(0, 35, Sort.by(Sort.Direction.ASC,"oid"));         //Integer.parseInt(10)
       Page<Eqp> allPage=eQPRepository.findAll(new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
             EntityGraph graph =emSei.getEntityGraph("Eqp.task");
            //emSei.createQuery("FROM Eqp", Eqp.class).getResultList()没.setHint(?)没法使用缓存的。
            List<Eqp> eqps = emSei.createQuery("FROM Eqp", Eqp.class)
                    .setHint("javax.persistence.fetchgraph", graph)
              //      .setFirstResult(11)
             //      .setMaxResults(20)
                    .getResultList();
            //getResultStream().limit(10).collect(Collectors.toList()) 已经全表取到内存来了;
    */
       List<Eqp>  eqps= allPage.getContent();
       return eqps;        //.subList(74070,74085);
    }

    //多数系统正常地，查询都是直接规定设计好了参数范围的模式，但是灵活性较差，参数个数和逻辑功能较为限制；但安全性好，就是代码上麻烦点。
    public Iterable<Eqp> findEQPLike(DeviceCommonInput filter) {
        return eQPRepository.findAll();
    }

    //orderBy 可支持直接指定某属性的下级字段。 {"orderBy": "pos.building",}
    public Iterable<Eqp> findAllEQPsFilterInput(DeviceCommonInput filter, int offset, int first, String orderBy, boolean asc) {
        Pageable pageable;

        if(StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc? Sort.Direction.ASC: Sort.Direction.DESC,orderBy));
        Page<Eqp> allPage=eQPRepository.findAll(new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(filter.getCod())) {
                 //   Path<Task> p = root.get("task");
                    Path<String> p = root.get("cod");
                    //非集合型的关联对象可以直接指定下一级的字段SingularAttribute。　该属性，Set，List，Map；//最新的一条TASK/Isp,规化成非集合。
                    //PluralAttribute 复数形式Set[];
          //          p = root.get("pos");
               //     Path<String> p2 = p.get("devs");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                   // p = root.get("isps");
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("fno");
                    predicateList.add(cb.like(p,"%" + filter.getOid() + "%"));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        }, pageable);

        List<Eqp>  eqps= allPage.getContent();
        return eqps;        //.subList(74070,74085)
    }

    public long countAllEQPsFilter(DeviceCommonInput filter) {
        Specification<Eqp> spec= new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(filter.getCod())) {
                    Path<String> p = root.get("type");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("fno");
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
            //Set<Eqp> eqps=item.getMaints();   懒加载,此时对象该字段还没有数据呢,手动查
           List<Eqp> eqps=eQPRepository.findByMtU(item);
           item.setMaints(new HashSet<Eqp>(eqps));
           //这里虽然懒加载因为业务需要单位直接关联它维保的设备列表=支持graphQL内省，免去再开独立接口函数去做这工作的复杂模式。
        });
        return units;
    }

    public Iterable<UserBase> findAllUsers() {
        List<User>  users=userRepository.findAll();
        List<UserBase>  parents = new ArrayList<UserBase>();
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
   public UserBase userBasic(Long id) {
       return userRepository.findById(id).orElse(null);
   }


    public Long countReport(Long ispId) {
        if (ispId == null) return reportRepository.count();
        Isp isp = iSPRepository.findById(ispId).orElse(null);
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
    //原型对应：getDevice(id:ID!): Eqp! 尽量不要返回值必选的，前端会报错，改成getDevice(id:ID!): EQP可选null。
    public Eqp getDevice(Long id) {
        return eQPRepository.findById(id).orElse(null);
        //因为LAZY所以必须在这里明摆地把它预先查询出来，否则graphQL内省该字段就没结果=报错; open-in-view也没效果。
        //单层eqp.getTask().stream().count();  //.collect(Collectors.toSet())
        //两层eqp.getTask().stream().forEach(t->t.getIsps().stream().count());
    }
    public EqpEs getEqpEs(Long id) {
        return eqpEsRepository.findById(id).orElse(null);
    }
    //前端路由乱来？不是正常的url也来这里了： java.lang.Long` from String "favicon.ico": not a valid Long value
    public Eqp getDeviceSelf(Long id) {
        Eqp eqp=eQPRepository.findById(id).orElse(null);
        // Assert.isTrue(eqp != null,"未找到EQP:"+id);
        return eqp;
    }

    public Iterable<SimpleReport> getReportOfISP(Long id) {
        Isp isp=iSPRepository.findById(id).orElse(null);
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

    public Eqp findEQPbyCod(String cod) {
        return eQPRepository.findByCod(cod);
    }
    public Iterable<CompanyEs> findUnitbyNameAnd(String name, String name2) {
        if(name2==null) name2="";
        Iterable<CompanyEs> list=companyEsRepository.findAllByNameQueryPhrase2(name,name2);
        return list;
    }
    public Iterable<CompanyEs> findUnitbyNameAnd2(String name, String name2) {
        Iterable<CompanyEs> list=companyEsRepository.findAllByNameSqueryPhrase2(name,name2);
        return list;
    }
    public Iterable<CompanyEs> findUnitbyName(String name) {
        Iterable<CompanyEs> list=companyEsRepository.findAllByNameContains(name);
        return list;
    }
    public Iterable<CompanyEs> findUnitbyName1(String name) {
        Iterable<CompanyEs> list=companyEsRepository.findAllByName_KeywordContains(name);
        return list;
    }
    public Iterable<PersonEs> findUnitbyName2(String name) {
        Iterable<PersonEs> list=personEsRepository.findAllByNameMatchePhrase(name);
        return list;
    }
    public Iterable<CompanyEs> findUnitbyNameArr(String[] names) {
        Iterable<CompanyEs> list=companyEsRepository.findAllByNameIn(names);
        return list;
    }
    public Iterable<CompanyEs> getUnitbyFilter(UnitCommonInput as) {
      //  Script script=new Script("2" );
        List<String> values=new LinkedList<>();
        values.add("电话号码");
        values.add("电话");
        TermsSetQueryBuilder termsSetQueryBuilder=new TermsSetQueryBuilder("phone",values);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQuery().must(
                        matchPhraseQuery("linkMen",as.getLinkMen()).slop(7)
                ).must(
                        termsSetQueryBuilder.setMinimumShouldMatchField("address")
                )
        ).build();
        IndexCoordinates indexCoordinates=esTemplate.getIndexCoordinatesFor(CompanyEs.class);
         // Stream<CompanyEs> list= esTemplate.stream(searchQuery, CompanyEs.class,indexCoordinates);
                //queryForList(searchQuery, CompanyEs.class,indexCoordinates);
        SearchHits<CompanyEs> searchHits = esTemplate.search(searchQuery, CompanyEs.class, indexCoordinates);
        List<SearchHit<CompanyEs>> hits=searchHits.getSearchHits();
        //Iterable<CompanyEs> list=esTemplate.search(searchQuery);
        Iterable<CompanyEs> list= (List<CompanyEs>) SearchHitSupport.unwrapSearchHits(hits);
        String sql=searchQuery.getQuery().toString();
        return list;
    }
    //就算异步的，也是需要分页参数，不同请求包context变动了?重新查询/数据源主动缓存加速。ES能对Filter过滤器部分自动缓存。
    public Iterable<CompanyEs> getCompanyEsbyFilter(UnitCommonInput as,Pageable pageable) {
         NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQuery().must(
                        matchPhraseQuery("name",as.getName()).slop(5)
                )
        ).withPageable(pageable).build();
        IndexCoordinates indexCoordinates=esTemplate.getIndexCoordinatesFor(CompanyEs.class);
        SearchHits<CompanyEs> searchHits = esTemplate.search(searchQuery, CompanyEs.class, indexCoordinates);
        SearchPage<CompanyEs> page= SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        SearchHits<CompanyEs> hits=page.getSearchHits();
        Iterable<CompanyEs> list= (List<CompanyEs>) SearchHitSupport.unwrapSearchHits(hits);
        return list;
    }
    public Iterable<CompanyEs> getCompanyEsbyFilter_旧版本(UnitCommonInput as,Pageable pageable) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.
                matchPhraseQuery("name",as.getName()).slop(5);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("date").from("2016-01-01 00:00:00");
        BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder().must(matchPhraseQueryBuilder);
        boolQueryBuilder.must(childBoolQueryBuilder).must(rangeQueryBuilder);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(childBoolQueryBuilder);
        searchSourceBuilder.from((int)pageable.getOffset());
        searchSourceBuilder.size(pageable.getPageSize());
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("company");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esTemplate.execute(
                client -> client.search(searchRequest, RequestOptions.DEFAULT)
        );
        int size=searchResponse.getHits().getHits().length;
        return null;   //这种方式其实也会主动返回总数的！　"hits":{"total":{"value":13,]
    }
    public Iterable<PersonEs> getPersonEsbyFilter(UnitCommonInput as,Pageable pageable) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQuery().must(
                        matchPhraseQuery("name",as.getName()).slop(0)
                )
        ).withPageable(pageable).build();
        IndexCoordinates indexCoordinates=esTemplate.getIndexCoordinatesFor(PersonEs.class);
        SearchHits<PersonEs> searchHits = esTemplate.search(searchQuery, PersonEs.class, indexCoordinates);
        SearchPage<PersonEs> page= SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        SearchHits<PersonEs> hits=page.getSearchHits();
        Iterable<PersonEs> list= (List<PersonEs>) SearchHitSupport.unwrapSearchHits(hits);
        return list;
    }
    public Iterable<?> getUnitEsFilter(UnitCommonInput as, int offset, int limit, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        if(limit<=0)   limit=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, limit);
        else
            pageable = PageOffsetFirst.of(offset, limit, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));
       if (as==null) return null;
        if(as.isCompany())
            return getCompanyEsbyFilter(as,pageable);
        else
            return getPersonEsbyFilter(as,pageable);
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

    public Iterable<Eqp> getAllEQP() {
        String partcod="05T1";
        String partoid="C8456";
        Specification<Eqp> spec= new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
        Specification<Eqp> spec= new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
        Specification<Eqp> spec= new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
           /*     if (!StringUtils.isEmpty(filter.getCod())) {
                    Path<String> p = root.get("type");
                    predicateList.add(cb.like(p,"%" + filter.getCod() + "%"));
                }
                if (!StringUtils.isEmpty(filter.getOid())) {
                    Path<String> p = root.get("fno");
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

    public Iterable<User> findAllUserFilter(DeviceCommonInput filter, int offset, int limit, String orderBy, boolean asc) {
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        QUser qm = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        if(null!=filter.getId()) {
            builder.and(qm.id.eq(filter.getId()));
        }
        Iterable<User> pool= userRepository.findAll(builder,pageable);
        return pool;
    }
    public Iterable<User> findAllUserFilter删除(WhereTree where, int offset, int first, String orderBy, boolean asc) {
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
    public Eqp findAllEQPsFilter222(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        //List<Elevator> list =elevatorRepository.findAll();
        return null;
    }
    //2020-8-18升级后不能用WhereTree来做前端查询了！input类型不直接使用Object Type呢？Object字段可能存在循环引用，或字段引用不能作为查询输入的接口和联合类型。
    //BUG导致graphQL input递归无法再使用！ModelFilters这层类sql接口预备给高权限场景使用WhereTree，普通接口走参数定做模式/多写代码。
    public Iterable<Eqp> findAllEQPsFilter_delete(WhereTree where, int offset, int first, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        ModelFiltersImpl<Eqp> modelFilters = new ModelFiltersImpl<Eqp>(null);
        Specification<Eqp> specification = new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
            Page<Eqp> findAll(@Nullable Specification<Eqp> spec, Pageable pageable);
        */
        Page<Eqp> list = eQPRepository.findAll(modelFilters,pageable);
        return list;
    }
    public Iterable<Eqp> findAllEQPsFilter_删除(DeviceCommonInput where, int offset, int first, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        //这里可以增加后端对　查询的权限控制，控制关注许可后的　某用户可以查询那些
        if(first<=0)   first=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, first);
        else
            pageable = PageOffsetFirst.of(offset, first, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        ModelFiltersImpl<Eqp> modelFilters = new ModelFiltersImpl<Eqp>(null);
        Specification<Eqp> specification = new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                modelFilters.effectCount(2000);    //像回调函数一样。
                return null;
            }
        };

        modelFilters.initialize(specification,emSei);
        //modelFilters.effectWhereTree(where);

        /* 这下面对照是这样的： query级缓存时间内，数据库人工修改的，前端靠findAllEQPsFilter查的会滞后才显示。
            注意query-results缓存时间内select from结果集不变，但是列表中某单一个实体的字段却可变动的；人工删除实体会报错。
            @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") } )
            Page<Eqp> findAll(@Nullable Specification<Eqp> spec, Pageable pageable);
        */
        Page<Eqp> list = eQPRepository.findAll(modelFilters,pageable);
        return list;
    }

    //普通接口为了安全只好写死代码介入过滤，不能依靠前端的输入参数WhereTree来过滤，前端可被用户随意修改的。
    public Iterable<Equipment> findAllEQPsFilter3(DeviceCommonInput where, int offset, int limit, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        if(limit<=0)   limit=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, limit);
        else
            pageable = PageOffsetFirst.of(offset, limit, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        QEqp qm = QEqp.eqp;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(where.getCod()))
            builder.and(qm.cod.contains(where.getCod()));
        if (where.getOwnerId()!=null)
            builder.and(qm.owner.id.eq(where.getOwnerId()));
        if (!StringUtils.isEmpty(where.getFno()))
            builder.and(qm.fno.contains(where.getFno()));

        List<Equipment>  elevators = new ArrayList<Equipment>();
        //Iterable<Eqp> eqps = eQPRepository.findAll(builder,pageable);
        Iterable<Eqp> eqps = eQPRepository.findAll(pageable);
        eqps.forEach(item -> {
           // if(item instanceof Equipment)
                elevators.add(item);
        });
        return elevators;
    }
    //即使用上缓存，通常不会提高性能，除非大家查的数据是相同的，否则一个参数条件变化就不能命中缓存，作用较为有限。
    public Iterable<Equipment> findAllEQPsFilter(DeviceCommonInput where, int offset, int limit, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        if(limit<=0)   limit=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, limit);
        else
            pageable = PageOffsetFirst.of(offset, limit, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        QEqp qm = QEqp.eqp;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(where.getCod()))
            builder.and(qm.cod.contains(where.getCod()));
        if (where.getOwnerId()!=null)
            builder.and(qm.owner.id.eq(where.getOwnerId()));
        if (!StringUtils.isEmpty(where.getFno()))
            builder.and(qm.fno.contains(where.getFno()));

        List<Equipment>  elevators = new ArrayList<Equipment>();
        Iterable<Eqp> eqps = eQPRepository.findAllNc(builder,pageable);
        eqps.forEach(item -> {
            // if(item instanceof Equipment)
            elevators.add(item);
        });
        return elevators;
    }
    public Iterable<Equipment> getAllEqpEsFilter(DeviceCommonInput where, int offset, int limit, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        if(limit<=0)   limit=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, limit);
        else
            pageable = PageOffsetFirst.of(offset, limit, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        List<Equipment>  elevators = new ArrayList<Equipment>();
/*
.must(
                        matchPhraseQuery("cod",where.getCod()).slop(9)
                         matchPhraseQuery("fno",where.getFno())
                )
                matchQuery("cod",where.getCod())
                matchPhraseQuery("cod",where.getCod()).slop(9)
        */
        BoolQueryBuilder    boolQueryBuilder=new BoolQueryBuilder();
        if (!StringUtils.isEmpty(where.getCod()))
            boolQueryBuilder.must(matchPhraseQuery("cod",where.getCod()).slop(9));
        if (!StringUtils.isEmpty(where.getFno()))
            boolQueryBuilder.must(matchPhraseQuery("fno",where.getFno()).slop(9));
        if (!StringUtils.isEmpty(where.getUseUid()))
            boolQueryBuilder.must(termQuery("useu.id",where.getUseUid()));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQueryBuilder
        ).withPageable(pageable).build();

        IndexCoordinates indexCoordinates=esTemplate.getIndexCoordinatesFor(EqpEs.class);
        // Stream<CompanyEs> list= esTemplate.stream(searchQuery, CompanyEs.class,indexCoordinates);
        //queryForList(searchQuery, CompanyEs.class,indexCoordinates);
        SearchHits<EqpEs> searchHits = esTemplate.search(searchQuery, EqpEs.class, indexCoordinates);
        List<SearchHit<EqpEs>> hits=searchHits.getSearchHits();
        //Iterable<CompanyEs> list=esTemplate.search(searchQuery);
        Iterable<EqpEs> list= (List<EqpEs>) SearchHitSupport.unwrapSearchHits(hits);
        String sql=searchQuery.getQuery().toString();
        list.forEach(item -> {
            elevators.add(item);
        });
        return elevators;
    }
    public Iterable<Equipment> getAllEqpEsFilter_保留(DeviceCommonInput where, int offset, int limit, String orderBy, boolean asc) {
        User user= checkAuth();
        if(user==null)   return null;
        if(limit<=0)   limit=20;
        Pageable pageable;
        if (StringUtils.isEmpty(orderBy))
            pageable = PageOffsetFirst.of(offset, limit);
        else
            pageable = PageOffsetFirst.of(offset, limit, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy));

        List<Equipment>  elevators = new ArrayList<Equipment>();
        //matchPhraseQuery("fno",where.getFno()).slop(9)    //matchQuery("fno",where.getFno())
        //wildcardQuery("cert.keyword",where.getCert())
       /*  .must(
               matchPhraseQuery("fno",where.getFno()).slop(9)
               matchPhraseQuery("cod",where.getCod()).slop(9)
        )

        .must(
                        wildcardQuery("cod.keyword",where.getCod())
                )
                wildcardQuery("fno.keyword",where.getFno())
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQuery().must(
                        wildcardQuery("fno",where.getFno())
                )
        ).build();
        */

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
                boolQuery().must(
                        wildcardQuery("cod.keyword",where.getCod())
                ).must(
                        wildcardQuery("fno.keyword",where.getFno())
                )
        ).withPageable(pageable).build();

        IndexCoordinates indexCoordinates=esTemplate.getIndexCoordinatesFor(EqpEs.class);
        // Stream<CompanyEs> list= esTemplate.stream(searchQuery, CompanyEs.class,indexCoordinates);
        //queryForList(searchQuery, CompanyEs.class,indexCoordinates);
        SearchHits<EqpEs> searchHits = esTemplate.search(searchQuery, EqpEs.class, indexCoordinates);
        List<SearchHit<EqpEs>> hits=searchHits.getSearchHits();
        //Iterable<CompanyEs> list=esTemplate.search(searchQuery);
        Iterable<EqpEs> list= (List<EqpEs>) SearchHitSupport.unwrapSearchHits(hits);
        String sql=searchQuery.getQuery().toString();

        list.forEach(item -> {
            elevators.add(item);
        });
        return elevators;
    }
    //通过ES搜到的Company或Person的id反过来映射unit_ID
    public Unit getUnit(Long esid,boolean company) {
        Unit unit;
        if(company)  unit=unitRepository.findUnitByCompany_Id(esid);
        else    unit=unitRepository.findUnitByPerson_Id(esid);
        return unit;
    }
    //graphQL接口名字是不允许重载的
    public Unit unit(Long id) {
       if(null==id) return null;
        Unit unit=unitRepository.findById(id).orElse(null);
        return unit;
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