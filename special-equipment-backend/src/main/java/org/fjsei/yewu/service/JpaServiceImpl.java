package org.fjsei.yewu.service;

import md.system.User;
import md.system.UserRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISP;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.Task;
import md.specialEqp.inspect.TaskRepository;
import md.cm.geography.Address;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.repository.Teacher;
import org.fjsei.yewu.repository.TeacherDao;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:13
 */

@Service
//@Qualifier("entityManagerFactoryBar")
@Transactional(value="transactionManager",readOnly=true)
public class JpaServiceImpl implements JpaService {

  //  @Autowired    private StudentDao studentDao;
    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;

    @Autowired
    private EqpRepository eQPRepository;
    @Autowired
    private ISPRepository iSPRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AddressRepository addressRepository;

  /*  @Override
    public Student findByName(String name) {
        return studentDao.findByName(name);
    }*/

    @Override
    public List<Teacher> getAllTeacher() {
        List<Teacher> topics = new ArrayList<>();
        teacherDao.findAll().forEach(topics::add);
        return topics;
    }

    //授权后：测试等批处理，任务入口。
   // @Transactional(value= "transactionManager",readOnly = true, transactionManager = "transactionManager")
    public Teacher getTeacher(String name) {
        Teacher teacher=new Teacher("hua","22","bvvnn2");
        if("addUsers".equals(name)) {
            addUsers();
            return teacher;
        }
        else if("batchAddETIU".equals(name)) {
            //batchAddETIU();
            return teacher;
        }


        return getTeacher_Cache(name);
    }

    public Teacher getTeacher_Cache(String name) {
        EntityManagerFactory entityManagerFactory=emSei.getEntityManagerFactory();
        Cache cache=entityManagerFactory.getCache();
        Teacher teacher=new Teacher("hua","22","bvvnn2");
        //EntityGraph graph =emSei.getEntityGraph("Eqp.task");      .setParameter( "id", 0L)
        System.out.println("1 createQuery");
        List<Eqp> eqps = emSei.createQuery("FROM EQP a where a.id = :name", Eqp.class)
                .setParameter( "name", 1L)
                .setHint(QueryHints.HINT_CACHEABLE, true)
                .getResultList();
        //.setHint( "javax.persistence.cache.storeMode", CacheStoreMode.REFRESH )  强制刷新;
        //.setHint( QueryHints.HINT_CACHE_REGION, "query.cache.person" )  细化控制

        String outprint="";
        System.out.println("Eqp a : eqps() kaishi");
        return teacher;
    }


    @Transactional
    public void addTeacher(Teacher topic) {
        if(!emSei.isJoinedToTransaction())      System.out.println("没达到 em.isJoinedToTransaction()");
        else System.out.println("到 em.isJoinedToTransaction()");
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Assert.isTrue(emSei.isJoinedToTransaction(),"没isJoinedToTransaction");

        Teacher teacher = teacherDao.save(topic);
         ///  teacherDao.flush();
        ///   emSei.merge(teacher);
        ///   emSei.flush();


    }
    @Transactional
    public void addUsers() {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        List<User> topics = new ArrayList<>();
        //teacherDao.findAll().forEach(topics::add);
        for(int j = 0; j < 1000; j++)  {
            User user = new User(getRandomString(10), "15");
            user.setPassword("g2346fvf");
            user.setMobile("546522242223343");
            topics.add(user);
        }
        userRepository.saveAll(topics);
        userRepository.flush();
        //    emSei.flush();
    }

    public static String getRandomString(int length){
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        //长度为几就循环几次
        for(int i=0; i<length; ++i){
            //产生0-61的数字
            int number=random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

}


