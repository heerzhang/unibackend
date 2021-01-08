package org.fjsei.yewu.resolver.sei.inspect;

import graphql.kickstart.tools.GraphQLMutationResolver;
import md.specialEqp.inspect.Isp;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.IspRepository;
import md.specialEqp.inspect.Task;
import md.specialEqp.inspect.TaskRepository;
import org.fjsei.yewu.exception.BookNotFoundException;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.security.JwtTokenUtil;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//实际相当于controller;
//这个类名字整个软件之内范围 不能重复 简名字！ baseMutation 是组件注入的名字。
//Mutation/Query本组内函数名字不能同名， 底层上做了HashMap找接口函数。
//GraphQL有非常重要的一个特点：强类型,自带graphQL基本类型标量Int, Float, String, Boolean和ID。　https://segmentfault.com/a/1190000011263214
//乐观锁确保任何更新或删除不会被无意地覆盖或丢失。悲观锁会在数据库级别上锁实体会引起DB级死锁。 https://blog.csdn.net/neweastsun/article/details/82318734

@Component
public class IspMgrMutation implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EqpRepository eQPRepository;
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


    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;                //EntityManager相当于hibernate.Session：

    @Autowired
    private final JwtTokenUtil jwtTokenUtil=new JwtTokenUtil();


    @Transactional
    public Isp newISP(Long devId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = new Isp();
        Eqp eQP = eQPRepository.findById(devId).orElse(null);
        Assert.isTrue(eQP != null,"未找到eQP:"+eQP);
        isp.setDev(eQP);
        iSPRepository.save(isp);
        return isp;
    }
    @Transactional
    public Isp buildISP(Long devId, Long taskId, String username) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        User user = userRepository.findByUsername(username);
        if(user == null)     throw new BookNotFoundException("没有该账户"+username, (long)0);
        Task task = taskRepository.findById(taskId).orElse(null);
        if(task == null)     throw new BookNotFoundException("没有该任务单", taskId);
        Eqp eQP = eQPRepository.findById(devId).orElse(null);
        if(eQP == null)     throw new BookNotFoundException("没有该设备", devId);
        Isp isp = new Isp();
        isp.setDev(eQP);
        isp.setTask(task);
        Set<User> ispMen= new HashSet<User>();
        //  ispMens.stream().forEach(item ->
        ispMen.add(user);
        isp.setIspMen(ispMen);
        //没有底下1行，在缓存时间之内，若以EQP起头查询关联将无法查到新isp，刷新URL也看不见新ISP，但getISPofDevTask查就可见。后端侧cache时间到了就都行了。
        task.getIsps().add(isp);
        //因为前端getDeviceSelf接口关联查询后读取路径是EQP.task.isps;而不是读EQP.isps字段，所以下面1行可以不搞；和接口使用者读的字段关联路径需求有直接关系。
        //eQP.getIsps().add(isp);
        iSPRepository.save(isp);
        return isp;
    }

    @Transactional
    public Task dispatchTaskTo(Long id, String status, String dep, String sdate) throws ParseException
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Task task = taskRepository.findById(id).orElse(null);
        Assert.isTrue(task != null,"未找到task:"+task);
        task.setStatus(status);
        task.setDep(dep);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        task.setDate(sdf.parse(sdate));
        taskRepository.save(task);
        return task;
    }

    @Transactional
    public Isp setISPispMen(Long id, List<Long> ispMens) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(id).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+isp);
        Set<User> ispMen= new HashSet<User>();
        ispMens.stream().forEach(item -> {
            User user= userRepository.findById(item).orElse(null);
            ispMen.add(user);
        });
        isp.setIspMen(ispMen);
        iSPRepository.save(isp);
        return isp;
    }

    @Transactional
    public Isp setISPtask(Long id, Long taskId) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(id).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+isp);
        Task task = taskRepository.findById(taskId).orElse(null);
        Assert.isTrue(task != null,"未找到task:"+task);
        isp.setTask(task);
        iSPRepository.save(isp);
        return isp;
    }

    @Transactional
    public Isp setISPreport(Long id, List<Long> reps) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(id).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+isp);
        reps.stream().forEach(item -> {
            Report report= reportRepository.findById(item).orElse(null);
            report.setIsp(isp);
        });
        iSPRepository.save(isp);    //对方维护关联关系，只做我方保存也可以。
        return isp;
    }

    @Transactional
    public boolean cancellationTask(Long taskId,String reason)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Task task = taskRepository.findById(taskId).orElse(null);
        Assert.isTrue(task != null,"未找到task:"+taskId);
        //这实体发现之间关联的情况越多的，删除就越麻烦咯，关系复杂。
        //ISP表与EQP都关联着Task的呢。  只好把ISP的清理当成了前置条件。
        Assert.isTrue(task.getIsps().isEmpty(),"还有ISP关联"+taskId);
        List<Eqp>  devs= task.getDevs();
        //解除关系
        devs.forEach(dev -> dev.getTask().remove(task));
        emSei.remove(task);
        emSei.flush();
        return task!=null;
    }

    @Transactional
    public boolean abandonISP(Long ispId,String reason)
    {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Isp isp = iSPRepository.findById(ispId).orElse(null);
        Assert.isTrue(isp != null,"未找到isp:"+ispId);
        //ispMen 解除关系
        Set<User>  mens= isp.getIspMen();
        mens.forEach(a -> a.getIsp().remove(isp));
        //若不解除关系不能立即给正确的应答，在缓存期限时间内做关联查找会报错某关联id找不到。
        //下面这两行若去掉一个都会导致关联id找不到，除非cache缓存时间过了才行，或者其他操作影响。
        isp.getTask().getIsps().remove(isp);
        isp.getDev().getIsps().remove(isp);

        emSei.remove(isp);
        emSei.flush();
        return isp!=null;
    }
}



/*
加了cache缓存后，为了在事务中读取数据库最新数据：emSei.find(Eqp.class,id)或eQPRepository.findById(id)或eQPRepository.getOne(id)或findAll()；
                或eQPRepository.findAll(new Specification<Eqp>() {@Override },pageable);
必须加  emSei.setProperty(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        emSei.setProperty(JPA_SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH); 加了这2条才能从DB去取最新数据。
而这些方法无需添加也能去数据库取最新数据：eQPRepository.findByCod(cod)或emSei.createQuery("")
*/

