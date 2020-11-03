package org.fjsei.yewu.service;

import md.system.User;
import md.system.UserRepository;
import md.specialEqp.*;
import org.fjsei.yewu.entity.sdn.Student;
import org.fjsei.yewu.entity.sdn.StudentDao;
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

    @Autowired
    private StudentDao studentDao;
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

    @Override
    public Student findByName(String name) {
        return studentDao.findByName(name);
    }

    @Override
    public List<Teacher> getAllTeacher() {
        List<Teacher> topics = new ArrayList<>();
        teacherDao.findAll().forEach(topics::add);
        return topics;
    }

   // @Transactional(timeout = 600000,value= "transactionManager",readOnly= true)
    public Teacher getTeacher_Read(String name) {
       // EntityTransaction entityTransaction=emSei.getTransaction();
       // entityTransaction.setRollbackOnly();
        EntityManagerFactory entityManagerFactory=emSei.getEntityManagerFactory();
        Cache cache=entityManagerFactory.getCache();
        Teacher teacher=new Teacher("hua","22","bvvnn2");
        //  teacherDao.save(topic);
            /*这：JPA普通查询，等价的graphQL; 实际测试效果差不多。
            query EQP_QUERY
            {
                findAllEQPs{
                cod  oid  task{
                    isps{
                        checkMen{ username }
                    }
                }
            }
            }  */

   //    List<Eqp> eqps=eQPRepository.findAll();   //"javax.persistence.fetchgraph" "javax.persistence.loadgraph"
        //？竟然没加Hint更快？
        EntityGraph graph =emSei.getEntityGraph("Eqp.task");
        System.out.println("1 createQuery");
        List<Eqp> eqps = emSei.createQuery("FROM EQP a", Eqp.class)
                .setHint("javax.persistence.loadgraph", graph)
                .getResultList();

    //     List<Eqp> eqps = emSei.createQuery("FROM Eqp a", Eqp.class).getResultList();

        String outprint="";
        System.out.println("Eqp a : eqps() kaishi");
        for (Eqp a : eqps) {
             outprint="作者 "
                    + a.getCod()
                    + " "
                    + a.getOid()
                    + " 书籍信息 "
                    + a.getTask()
                    .stream()
                    .map(b ->"{"+
                            (!b.getIsps().isEmpty()  ?
                                    b.getIsps().stream().map(s->"@"+
                                            (    s.getId()
                                            )
                                            +"@"
                                    ).collect(Collectors.joining("$ "))  : "没isp "
                            )
                            +"}"
                    )
                    .collect(Collectors.joining("; ") );
        }

        System.out.println("return ing! !");
        return teacher;
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
            batchAddETIU();
            return teacher;
        }
        else if("read".equals(name)) {
            return getTeacher_Read(name);
        }
        else if("addPositions".equals(name)) {
            addPositions();
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
    @Transactional
    public void addPositions() {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        List<Address> topics = new ArrayList<>();
        Random random=new Random();
        for(int j = 0; j < 5000; j++)  {
            int uid=random.nextInt(19)+1 +1000;
            Address user = new Address();
            //,"35"+uid, getRandomString(12)
            user.setName(getRandomString(24));
            topics.add(user);
        }
        addressRepository.saveAll(topics);
        addressRepository.flush();
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
    public void batchAddETIU() {
        for(int i=0; i<10; ++i){
            batchAddETIU_Sub();
        }
    }

    @Transactional(value= "transactionManager",readOnly=false)
    public void batchAddETIU_Sub() {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Random random=new Random();
        List<Eqp> topics = new ArrayList<>();
        //teacherDao.findAll().forEach(topics::add);
        for(int e = 0; e < 500; e++)  {
            int uid5=random.nextInt(170)+1 +3000;
            Eqp eQP =Eqp.builder().cod(getRandomString(6)+"越A").type(String.valueOf(uid5)).oid("起重"+getRandomString(6)).build();
         //   Set<Task> task_list= new HashSet<>();  //List<Task> task_list = new ArrayList<>();
            for(int t = 0; t < 0; t++) {
                Task task = new Task();
                List<Eqp> devs = new ArrayList<>();
                devs.add(eQP);
                task.setDevs(devs);

                for (int i = 0; i < 0; i++) {
                    ISP isp = new ISP();
                    isp.setDev(eQP);
                    isp.setTask(task);
                    Set<User> ispMen = new HashSet<User>();

                    int uid=random.nextInt(999)+1;    //生成[1,1000]区间的整数
                    User user1 = userRepository.getOne((long)uid);     //findById(userid).orElse(null);
                    uid=random.nextInt(999)+1;
                    User user2 = userRepository.getOne((long)uid);
                    uid=random.nextInt(999)+1;
                    User user3 = userRepository.getOne((long)uid);
                    //  ispMens.stream().forEach(item ->
                    ispMen.add(user2);
                    ispMen.add(user1);
                    isp.setIspMen(ispMen);
                    isp.setCheckMen(user3);
                    iSPRepository.save(isp);
                }
                taskRepository.save(task);
           //     task_list.add(task);
            }
            //eQP.setTask(task_list);
            eQP.setFNo("越F"+getRandomString(10));
            int uid8=random.nextInt(1999)+1 +1293333;
            Address position=addressRepository.getOne((long)uid8);
            eQP.setPos(position);
            topics.add(eQP);
        }
        eQPRepository.saveAll(topics);
        emSei.flush();
    }

}


