package org.fjsei.yewu.resolver.sei;

import com.alibaba.fastjson.JSON;
import graphql.kickstart.tools.GraphQLMutationResolver;
import md.cm.base.Company;
import md.cm.base.CompanyRepository;
import md.cm.base.Person;
import md.cm.base.PersonRepository;
import md.specialEqp.inspect.Isp;
import md.specialEqp.type.Elevator;
import md.specialEqp.type.ElevatorRepository;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.Unit;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import org.fjsei.yewu.entity.fjtj.HrUserinfo;
import org.fjsei.yewu.entity.fjtj.HrUserinfoRepository;
import md.specialEqp.inspect.IspRepository;
import md.specialEqp.inspect.Task;
import md.specialEqp.inspect.TaskRepository;
import org.fjsei.yewu.exception.BookNotFoundException;
import org.fjsei.yewu.index.sei.*;
import org.fjsei.yewu.input.DeviceCommonInput;
import md.cm.geography.*;
import org.fjsei.yewu.input.UnitCommonInput;
import org.fjsei.yewu.security.JwtTokenUtil;
import org.fjsei.yewu.security.JwtUser;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_RETRIEVE_MODE;
import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_STORE_MODE;


//实际相当于controller;
//这个类名字不能重复简明！
//GraphQL有非常重要的一个特点：强类型,自带graphQL基本类型标量Int, Float, String, Boolean和ID。　https://segmentfault.com/a/1190000011263214
//乐观锁确保任何更新或删除不会被无意地覆盖或丢失。悲观锁会在数据库级别上锁实体会引起DB级死锁。 https://blog.csdn.net/neweastsun/article/details/82318734

@Component
public class BaseMutation implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EqpRepository eQPRepository;
    @Autowired
    private EqpEsRepository eqpEsRepository;
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
    private TownRepository townRepository;
    @Autowired
    private AdminunitRepository adminunitRepository;
    @Autowired
    private HrUserinfoRepository hrUserinfoRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired   private CompanyEsRepository companyEsRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired   private PersonEsRepository personEsRepository;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;                //EntityManager相当于hibernate.Session：

    @Autowired
    private final JwtTokenUtil jwtTokenUtil=new JwtTokenUtil();

    @Value("${sei.cookie.domain:}")
    private final String  cookieDomain="";
    @Value("${server.ssl.enabled:false}")
    private boolean  isSSLenabled;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public Eqp newEQP(String cod, String type, String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eQP =Eqp.builder().cod(cod).type(type).oid(oid).build();
        //这样无法执行Set<Task> task=new HashSet<>();原来new Eqp()却可以的。
        eQP.setSort("三方大的");
        eQP.setVart("Ccvs第三方大师傅得f");
        Task task=new Task();
        task.setDep("12111kk234fv");
        Set<Task> tasks=new HashSet<>();
        tasks.add(task);
        eQP.setTask(tasks);     //getTask().add(task);
        List<Eqp> devs=new ArrayList<>();
        devs.add(eQP);
        //多对多保存复杂一点，必须都给set上。
        task.setDevs(devs);

        Task task2=new Task();
        task2.setDep("aAxwxxxx3f4fv");
        eQP.getTask().add(task2);
        List<Eqp> devs2=new ArrayList<>();
        devs2.add(eQP);
        task2.setDevs(devs2);

        task.setStatus("STOPted");
        task2.setStatus("RuningYe");
        try {
            taskRepository.save(task);
            taskRepository.save(task2);
            eQPRepository.saveAndFlush(eQP);
            //这里保存若事务异常就导致下面ES更新无法回滚了。
            //Eqp sec = eQP instanceof Elevator ? ((Elevator) eQP) : null;
            //if( !(sec instanceof Elevator) )       return eQP;
            //TODO: flush tasks ... 尽量确保JPA不出现异常否则ES就不一致了。
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
       // EqpEs eqpEs=new EqpEs();
        //嵌套对象会直接引用ref的=浅层拷贝。嵌套的关联对象给Elasticsearch也是全量都存储的。
      //直接用BeanUtils.copyProperties(eQP,eqpEs); 　 若有字段嵌套对象类型不一样的就会报错。
        String json = JSON.toJSONString(eQP);
        EqpEs eqpEs=JSON.parseObject(json, EqpEs.class);   //遇到某字段的属于嵌套对象类型并且该字段对象类型还变化的情况也可顺利转换。
    //    eqpEs.getTask().forEach(item -> {
   //     });
        //ES不支持事务与回滚。
        //相互关联导致的ES存储死循环：EqpEs->task->Eqp->task,这样task id自循环导致。表现为newEQP函数上事务上死锁。
        eqpEsRepository.save(eqpEs);
        //这个时间保存ES若异常可自动取消eQPRepository.saveAndFlush的操作结果。
        //运行到这还处于Transactional范围下，可是Elasticsearch已经发给服务器去处理了，JPA却还没有交给数据库去存储。
        return eQP;
    }

    //Town + address
    @Transactional
    public Adminunit initAdminunit(Long townId, String prefix, String areacode, String zipcode) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Town town = townRepository.findById(townId).orElse(null);
        Assert.isTrue(town != null,"未找到town:"+townId);
        Adminunit adminunit=adminunitRepository.findByTownIs(town);
        if(adminunit==null){
            adminunit=new Adminunit();
            adminunit.setTown(town);
            adminunit.setCounty(town.getCounty());
            adminunit.setCity(town.getCounty().getCity());
            adminunit.setProvince(town.getCounty().getCity().getProvince());
            adminunit.setCountry(town.getCounty().getCity().getProvince().getCountry());
        }
        adminunit.setPrefix(prefix);
        adminunit.setAreacode(areacode);
        adminunit.setZipcode(zipcode);
        adminunitRepository.save(adminunit);
        return adminunit;
    }
    @Transactional
    public Address newPosition(String name, Long aduId, String lat, String lon) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Adminunit adminunit=adminunitRepository.findById(aduId).orElse(null);
        //Adminunit adminunit=adminunitRepository.findByTownIs(town);
        Assert.isTrue(adminunit != null,"未找到adminunit:"+aduId);
        Address position = new Address();
        position.setName(name);
        position.setAd(adminunit);
        position.setLatAndLon(lat, lon);
        addressRepository.save(position);
        return position;
    }

    @Transactional
    public Report newReport(Long ispId, String modeltype, String modelversion) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(ispId).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+ispId);
        //Todo: 报告编码
        String repNo="JD2020FTC00004";
        Report report = new Report("电梯定检",repNo,isp);
        report.setModeltype(modeltype);
        report.setModelversion(modelversion);
        java.util.Date  now= new java.util.Date();
        //report.setUpLoadDate(new java.sql.Date(now.getTime()));
        report.setUpLoadDate(now);
        reportRepository.save(report);
        return report;
    }

    @Transactional
    public Report buildReport(Long ispId, String no, String path) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(ispId).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+isp);
        Report report = new Report(path,isp,no);
        reportRepository.save(report);
        return report;
    }

    @Transactional
    public Task newTask(Long devsId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eQP = eQPRepository.findById(devsId).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        Task task = new Task();
        List<Eqp> devs=new ArrayList<>();
        devs.add(eQP);
        task.setDevs(devs);
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task buildTask(Long devsId, String dep, String sdate) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(sdate, pos);
        if(date == null) {
            throw new BookNotFoundException("日期like非法？", devsId);
            //前端应会得到这个反馈结果，　{data: null，errors: [{message: "日期like非法？", }]  }
        }
        Eqp eQP = eQPRepository.findById(devsId).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        Task task = new Task();
        List<Eqp> devs=new ArrayList<>();
        devs.add(eQP);
        task.setDevs(devs);
        task.setDep(dep);
        task.setDate(date);
        taskRepository.save(task);
        //在前端上无法立刻更新，看不见新任务啊；加了底下2行点刷新URL可立刻看见。
        eQP.getTask().add(task);
        eQPRepository.save(eQP);
        return task;
    }

    @Transactional
    public Task addDeviceToTask(Long taskId, Long devId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Task task = taskRepository.findById(taskId).orElse(null);
        Assert.isTrue(task != null,"未找到task:"+task);
        Eqp eQP = eQPRepository.findById(devId).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        task.getDevs().add(eQP);
        taskRepository.save(task);
        return task;
    }
    /*Todo: 当ES保存异常时，重试会影响业务的响应即时性。若ES异常，在RMDB数据库插入一条补救任务，
        有Worker任务会实时地扫这些数据，以RMDB为基准更新ES,通过此补偿机制，来保证ES与RMDB的最终一致性。
    */
    @PreAuthorize("hasRole('Ma')")
    @Transactional(rollbackFor = Exception.class)
    public Unit newUnit(String name, String address) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Unit unit = new Unit(name, address);
        try {
            unitRepository.saveAndFlush(unit);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        CompanyEs companyEs =new CompanyEs();
        BeanUtils.copyProperties(unit, companyEs);
        companyEs.setLinkMen("何尔章");
        companyEs.setPhone("电话号码");
        companyEsRepository.save(companyEs);
        return unit;
    }
    //仅用于测试的；
    @Transactional(rollbackFor = Exception.class)
    public Unit newUnitCompany(UnitCommonInput upar, Long id) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Company company=new Company();
        BeanUtils.copyProperties(upar,company);
        Unit unit = new Unit();
        unit.setCompany(company);
        try {
            companyRepository.saveAndFlush(company);
            unitRepository.saveAndFlush(unit);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        CompanyEs companyEs=new CompanyEs();
        BeanUtils.copyProperties(company,companyEs);
        companyEsRepository.save(companyEs);
        return unit;
    }
    @Transactional(rollbackFor = Exception.class)
    public Unit newUnitPerson(UnitCommonInput upar, Long id) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Person company=new Person();
        BeanUtils.copyProperties(upar,company);
        Unit unit = new Unit();
        unit.setPerson(company);
        try {
            personRepository.saveAndFlush(company);
            unitRepository.saveAndFlush(unit);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        PersonEs personEs=new PersonEs();
        BeanUtils.copyProperties(company,personEs);
        personEsRepository.save(personEs);
        return unit;
    }
    @Transactional(rollbackFor = Exception.class)
    public Unit newUnitExternalSource(UnitCommonInput upar) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        if(upar.isCompany())
            return   newUnitCompany(upar,0L);
        else
            return   newUnitPerson(upar,0L);
    }
    //无需登录授权访问的特殊函数，graphQL不要返回太多内容如User;
    @Transactional
    public boolean newUser(String username,String password,String mobile,String external,String eName,String ePassword)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        //前置条件验证,OAuth2 外部的系统用户直接授权的情形。
        if(external.equals("旧平台")){
            String err="";
            HrUserinfo hrUser=hrUserinfoRepository.findByUserIdEquals(eName);
            if(hrUser==null)    err="无此用户";
            else if(!hrUser.getPassword().equals(ePassword))    err="密码不对";
            else if(hrUser.getStatus()!=2)    err="非正常用户";
            if(!err.equals(""))  throw new BookNotFoundException("旧平台验证失败"+err,14L);
        }
        else return false;
        User user = new User(username, null);
        //Todo： 账户重名的验证。
        String dbmima=passwordEncoder.encode(password);
        user.setPassword(dbmima);
        user.setMobile(mobile);
        user.setAuthName(eName);
        user.setAuthType(external);
        user.set旧账户(eName);
        userRepository.save(user);
        return true;    //都是成功，数据库保存不成功？ 底层就报错;
    }

    @Transactional
    public boolean logout() {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)  return false;
        //"注销时，POST没有附带上 Bearer " 的，没有token,到底是谁啊,需要从OPTIONS才有知晓；或Cookie: token=；
        Object principal=auth.getPrincipal();
        if(principal instanceof JwtUser) {
            Long userid = ((JwtUser) principal).getId();
            User user = userRepository.findById(userid).orElse(null);
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername( user.getId().toString() );
            logger.info("user logout '{}', 注销退出了", user.getUsername());
            userRepository.save(user);
        }
        //没有登录的也Authenticated! 有anonymousUser 有ROLE_ANONYMOUS；
        SecurityContextHolder.getContext().setAuthentication(null);
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        Cookie cookie =new Cookie("token", "");
       // cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(1);
        cookie.setPath("/");
        cookie.setSecure(isSSLenabled);
        response.addCookie(cookie);
        return true;
    }
    //graphQL操作返回结果类型不同的，要到调用的时候才能报错的。
    //用户登录：
    @Transactional
    public boolean authenticate(String name,String password) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user = userRepository.findByUsername(name);
        if(user==null){
            Assert.isTrue(user != null,"没用户:"+name);
            //todo:　异常处理人性化
        }
        //经验证BCryptPasswordEncoder只能支持最大72个字符密码长度，后面超过部分被直接切除掉了。
        boolean isMatch=passwordEncoder.matches(password,user.getPassword());
        if(!isMatch){
            Assert.isTrue(isMatch,"密码错:"+name);
        }
        logger.debug("checking authentication for user '{}'", name);
        if(user==null)  return false;
        if (name != null ) {         //&& SecurityContextHolder.getContext().getAuthentication() == null
            logger.debug("security context was null, so authorizating user");
            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername( user.getId().toString() );
            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            String token = jwtTokenUtil.generateToken(userDetails);     //jwtTokenUtil.validateToken(authToken, userDetails)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
           HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

           authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            logger.info("authorizated user '{}', setting security context", name);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //浏览器自动遵守标准：超时的cookie就不会该送过来了。 那万一不守规矩？两手准备。
           HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
           Cookie cookie =new Cookie("token", token);
           //Domain是针对后端服务器，前端跨域，浏览器处理时和前端是那个的一点关系都没有。
            //若undertow不能加域名IP, 而tomcat可以加IP的。
            //cookie.setDomain(cookieDomain);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(5400);      //这个时间和token内部声称的时间不同，这给浏览器用的 = 1.5个小时。
            //Path就是servlet URL的接口路径，不能嵌套在其它servlet的底下;
            cookie.setPath("/");
            cookie.setSecure(isSSLenabled);     //只有HTTPS SSL才允許設。
           response.addCookie(cookie);
        }
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        Long  userid= ((JwtUser)(auth.getPrincipal())).getId();
        //返回权限列表
        return true;
    }

    @Transactional
    public Eqp setEQPPosUnit(Long id, Long posId, Long ownerId, Long maintId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eQP = eQPRepository.findById(id).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        Address position= addressRepository.findById(posId).orElse(null);
        Unit ownerUnit= unitRepository.findById(ownerId).orElse(null);
        Unit maintUnit= unitRepository.findById(maintId).orElse(null);
        Assert.isTrue(position != null,"未找到position:"+position);
        Assert.isTrue(ownerUnit != null,"未找到ownerUnit:"+ownerUnit);
        eQP.setPos(position);
        eQP.setOwner(ownerUnit);
        eQP.setMtu(maintUnit);
        eQPRepository.save(eQP);
        return eQP;
    }

    //设置基本设备信息; 参数和模型定义的同名接口的输入类型按顺序一致，否则Two different classes used for type
    @Transactional
    public Eqp buildEQP(Long id, Long ownerId, DeviceCommonInput info) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eQP = eQPRepository.findById(id).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        //Todo: 行政部分+用户定义名
        Address position= addressRepository.findByName(info.getAddress());
        if(position==null){
            position=new Address();
            //Todo: 行政部分 独立了。
            position.setName(info.getAddress());
            addressRepository.save(position);
        }
        Unit ownerUnit= unitRepository.findById(ownerId).orElse(null);
        Assert.isTrue(position != null,"未找到position:"+position);
        Assert.isTrue(ownerUnit != null,"未找到ownerUnit:"+ownerUnit);
        eQP.setPos(position);
        eQP.setOwner(ownerUnit);
        //修改数据的特别权限控制嵌入这里：
        eQP.setCod(info.getCod());
        eQP.setOid(info.getOid());
        eQPRepository.save(eQP);
        return eQP;
    }
    @Transactional
    public Elevator buildElevator_222(Long id, Long ownerId, DeviceCommonInput info) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eQP = eQPRepository.findById(id).orElse(null);
        Assert.isTrue(eQP == null,"找到eQP:"+eQP);
        Elevator elevator =Elevator.builder().cod(info.getCod()).type("typ01").oid(info.getOid()).build();
        //elevator.setLiftHeight("565555");
        elevatorRepository.save(elevator);
        return elevator;
    }
    @Transactional
    public Elevator buildElevator(String cod, String type, String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Elevator elevator =Elevator.builder().cod(cod).type(type).oid(oid).build();
        //elevator.setLiftHeight("565555");
        elevatorRepository.save(elevator);
        return elevator;
    }

    @Transactional
    public Eqp testEQPModify(Long id, String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        String  prevOid="is null";
        Map<String, Object>  properties1=emSei.getProperties();
        emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        Eqp eQP =eQPRepository.findById(id).orElse(null);
        if(eQP==null)   return  eQP;
        prevOid=eQP.getOid();
        eQP.setOid(oid);
        eQPRepository.save(eQP);
        return  eQP;
    }
    @Transactional
    public String testEQPFindModify(String cod,String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        String  prevOid="is null";
        Map<String, Object>  properties1=emSei.getProperties();
        Eqp eQP = eQPRepository.findByCod(cod);
        if(eQP==null)   return  prevOid;
        prevOid=eQP.getOid();
        eQP.setOid(oid);
        eQPRepository.save(eQP);
        return  prevOid;
    }
    @Transactional
    public String testEQPStreamModify222(String cod,String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        String  prevOid="not find,is null?";
        emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        List<Eqp> eqpList= eQPRepository.findAll();     //较慢:所有数据都装载了
        List<Eqp> eqpObjs=eqpList.stream().filter(e -> e.getCod().equals(cod)).collect(Collectors.toList());
        for (Eqp eQP:eqpObjs)
        {
            prevOid = eQP.getOid();
            eQP.setOid(oid);
            eQPRepository.save(eQP);
        }
        if(eqpObjs.size()>1)    prevOid="超过1个的eqp?";
        return  prevOid;
    }
    @Transactional
    public String testEQPStreamModify(String cod,String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        String  prevOid="not find,is null?";
        //必须加这2行，否则可能无法获取最新DB数据，可能不被认定为必须做DB更新。
        emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        //虽然findAll()被注解@QueryHints(HINT_CACHEABLE,true)了！　可是这里是不会从缓存读的，都会直接查数据库。
        List<Eqp> eqpList= eQPRepository.findAll();     //较慢:所有数据都装载了
        List<Eqp> eqpObjs=eqpList.stream().filter(e -> e.getCod().equals(cod)).collect(Collectors.toList());
        for (Eqp eQP:eqpObjs)
        {
            prevOid = eQP.getOid();
     //       if(eQP instanceof Elevator)
     //           ((Elevator) eQP).setLiftHeight("ss231");
            eQPRepository.save(eQP);
        }
        if(eqpObjs.size()>1)    prevOid="超过1个的eqp?";
        return  prevOid;
    }
    @Transactional
    public Eqp testEQPCriteriaModify(String cod, String oid, String type) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        //必须加这2行，否则可能无法获取最新DB数据，可能不被认定为必须做DB更新。
        emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        String  prevOid="not find,is null?";
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC,"oid"));         //Integer.parseInt(10)
        Page<Eqp> allPage=eQPRepository.findAll(new Specification<Eqp>() {
            @Override
            public Predicate toPredicate(Root<Eqp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (StringUtils.hasLength(cod)) {
                    Path<String> p = root.get("cod");
                    predicateList.add(cb.like(p,"%" + cod + "%"));
                }
                if (StringUtils.hasLength(type)) {
                    Path<String> p = root.get("type");
                    predicateList.add(cb.equal(p,type));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicateList.toArray(predicates);
                query.where(predicates);
                return null;
            }
        }, pageable);
        List<Eqp>  eqpObjs= allPage.getContent();
        for (Eqp eQP:eqpObjs)
        {
            prevOid = eQP.getOid();
            eQP.setOid(oid);
            eQPRepository.save(eQP);     //若缓存数据没有变换，这个不一定会提交给数据库？等于没干活。
        }
        if(eqpObjs.size()>1)    prevOid="超过1个的eqp?";
        return  eqpObjs.get(0);
    }
    @Transactional
    public String testEQPcreateQueryModify(String cod,String oid) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        String  prevOid="is null";
        Map<String, Object>  properties1=emSei.getProperties();
        List<Eqp> eQPs = emSei.createQuery(
                "select DISTINCT e from EQP e where id=2119", Eqp.class)
                .getResultList();
        Eqp eQP= eQPs.get(0);
        if(eQP==null)   return  prevOid;
        prevOid=eQP.getOid();
        eQP.setOid(oid);
        eQPRepository.save(eQP);
        Map<String, Object>  properties2=emSei.getProperties();
        return  prevOid;
    }

    @Transactional
    public boolean deleteReport(Long repId,String reason)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Report report = reportRepository.findById(repId).orElse(null);
        Assert.isTrue(report != null,"未找到Report:"+repId);
        Assert.isTrue(report.getFiles().isEmpty(),"还有File关联"+repId);
        emSei.remove(report);
        emSei.flush();
        return report!=null;
    }

    @Transactional
    public boolean invalidateEQP(Long eqpId,String reason)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eqp = eQPRepository.findById(eqpId).orElse(null);
        Assert.isTrue(eqp != null,"未找到EQP:"+eqpId);
        eqp.setValid(false);
        eQPRepository.save(eqp);
        return eqp!=null;
    }
    @Transactional
    public boolean removeEQP(Long eqpId)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Eqp eqp = eQPRepository.findById(eqpId).orElse(null);
        Assert.isTrue(eqp != null,"未找到EQP:"+eqpId);
        eQPRepository.delete(eqp);
        boolean hasEqp2 = eqpEsRepository.existsById(eqpId);
        if(hasEqp2)   eqpEsRepository.deleteById(eqpId);
        //eQPRepository.flush();
        return eqp!=null;
    }
}



/*
加了cache缓存后，为了在事务中读取数据库最新数据：emSei.find(Eqp.class,id)或eQPRepository.findById(id)或eQPRepository.getOne(id)或findAll()；
                或eQPRepository.findAll(new Specification<Eqp>() {@Override },pageable);
必须加  emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH); 加了这2条才能从DB去取最新数据。
而这些方法无需添加也能去数据库取最新数据：eQPRepository.findByCod(cod)或emSei.createQuery("")
*/

