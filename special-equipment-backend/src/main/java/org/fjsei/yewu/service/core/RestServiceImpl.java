package org.fjsei.yewu.service.core;

import md.specialEqp.EQP;
import md.specialEqp.EQPRepository;
import org.fjsei.yewu.repository.Teacher;
import org.fjsei.yewu.repository.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:13
 */

@Service
//@Qualifier("entityManagerFactoryBar")
//@Transactional
public class RestServiceImpl implements RestService {

    @Autowired
    private EQPRepository eqpRepository;
    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emBar;

  /*  @PersistenceContext(name = "entityManagerFactoryBar")
    public void SetEntityManager(EntityManager em) {
        this.em = em;
    } */

    @Override
    public Page<EQP> findByName_Page(String name) {
        //根据id 进行降序
        Sort.Order order =  new Sort.Order(Sort.Direction.DESC,"id");
    /* 作废
        Sort sort = new Sort(order);
        //index 1 从0开始 不是从1开始的
        Pageable page = new PageRequest(0,10,sort);
        Page<EQP> employeeList = eqpRepository.findAll(page);
        System.out.println("查询总页数:"+employeeList.getTotalPages());
        System.out.println("查询总记录数:"+employeeList.getTotalElements());
        System.out.println("查询当前第几页:"+employeeList.getNumber()+1);
        System.out.println("查询当前页面的集合:"+employeeList.getContent());
        System.out.println("查询当前页面的记录数:"+employeeList.getNumberOfElements());
        return employeeList;
        */
        return null;
    }

    public EQP findByName_ExampleMatcher(String name) {
        EQP eqp1=eqpRepository.findByCod(name);
        emBar.detach(eqp1);
        eqp1.setId(null);
        eqp1.setType(null);
        eqp1.setFactoryNo(null);
        eqp1.setCod(null);
        eqp1.setPos(null);
        eqp1.setTask(null);
        eqp1.setIsps(null);
        //     EQP eqp2=new EQP();
        //     eqp2.setOid(eqp1.getOid());

        //     eqp2.getOwnerUnt().setName(eqp1.getOwnerUnt().getName());
        //    Position pos=new Position();
        //    eqp2.setPos(pos.setAddress( (eqp1.getPos().getAddress() )));
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith()) //姓名采用“开始匹配”的方式查询
                .withIgnorePaths("focus");  //忽略属性：是否关注。因为是基本类型，需要忽略掉

        //创建实例
        Example<EQP> ex = Example.of(eqp1, matcher);

        //查询      ? id+ ! X对一关联表 字段也会算比较条件
        List<EQP> ls = eqpRepository.findAll(ex);

        //输出结果
        System.out.println("数量："+ls.size());
        for (EQP bo:ls)
        {
            System.out.println(bo.getCod());
        }

    /* 作废
        Sort.Order order =  new Sort.Order(Sort.Direction.DESC,"id");
        Sort sort = new Sort(order);
        //index 1 从0开始 不是从1开始的
        Pageable page = new PageRequest(0,10,sort);
        Page<EQP> employeeList = eqpRepository.findAll(page);
        System.out.println("查询总页数:"+employeeList.getTotalPages());
        System.out.println("查询总记录数:"+employeeList.getTotalElements());
        System.out.println("查询当前第几页:"+employeeList.getNumber()+1);
        System.out.println("查询当前页面的集合:"+employeeList.getContent());
        System.out.println("查询当前页面的记录数:"+employeeList.getNumberOfElements());

        EntityManagerFactory entityManagerFactory= ((HibernateEntityManagerFactory)emBar.getEntityManagerFactory());
        SessionFactory sessionFactory =((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
        Statistics statistics=sessionFactory.getStatistics();
    */
        return null;//employeeList;
    }

    @Override
    public EQP findByName(String name) {
        EQP eqp1=eqpRepository.findByCod(name);
   //     emBar.detach(eqp1);
   //     EQP eqp2=new EQP();
   //     eqp2.setOid(eqp1.getOid());

   //     eqp2.getOwnerUnt().setName(eqp1.getOwnerUnt().getName());
    //    Position pos=new Position();
    //    eqp2.setPos(pos.setAddress( (eqp1.getPos().getAddress() )));
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith()) //姓名采用“开始匹配”的方式查询
                .withIgnorePaths("focus");  //忽略属性：是否关注。因为是基本类型，需要忽略掉

        //创建实例
        Example<EQP> ex = Example.of(eqp1, matcher);

        //查询      ? id+ ! X对一关联表 字段也会算比较条件
        List<EQP> ls = eqpRepository.findAll(ex);

        //输出结果
        System.out.println("数量："+ls.size());
        for (EQP bo:ls)
        {
            System.out.println(bo.getCod());
        }
        /*作废！
        EntityManagerFactory entityManagerFactory= ((HibernateEntityManagerFactory)emBar.getEntityManagerFactory());
        SessionFactory sessionFactory =((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();
        Statistics statistics=sessionFactory.getStatistics();
        Cache cache=sessionFactory.getCache();
        */
        return null;//employeeList;
    }

    @Override
    public Page<EQP> findByName_Example(String name) {

        Sort.Order order =  new Sort.Order(Sort.Direction.ASC,"id");
      /* 作废
        Sort sort = new Sort(order);
        //index 1 从0开始 不是从1开始的
        Pageable page = new PageRequest(0,10,sort);

        Page<EQP> employeeList = eqpRepository.findAll(new Specification<EQP>() {

            public Predicate toPredicate(Root<EQP> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> codPath = root.get("cod");
                Path<String> oidPath = root.get("oid");

              //连接查询条件, 不定参数，可以连接0..N个查询条件

                query.where(cb.like(codPath, "%电梯%"), cb.like(oidPath, "%12%")); //这里可以设置任意条查询条件

                return null;
            }

        }, page);

        System.out.println("查询总页数:"+employeeList.getTotalPages());
        System.out.println("查询总记录数:"+employeeList.getTotalElements());
        System.out.println("查询当前第几页:"+employeeList.getNumber()+1);
        System.out.println("查询当前页面的集合:"+employeeList.getContent());
        System.out.println("查询当前页面的记录数:"+employeeList.getNumberOfElements());
        */
        return null;//eqpRepository.findByCod(name);
    }

    @Override
    public List<Teacher> getAllTeacher() {
        List<Teacher> topics = new ArrayList<>();
        teacherDao.findAll().forEach(topics::add);
        return topics;
    }

    @Transactional
    public Teacher getTeacher(String name) {
        Teacher topic=new Teacher("hua","22","bvvnn2");
        teacherDao.save(topic);
        return topic;
    }

    @Transactional
    public void addTeacher(Teacher topic) {
        if(!emBar.isJoinedToTransaction())      System.out.println("没达到 em.isJoinedToTransaction()");
        else System.out.println("到 em.isJoinedToTransaction()");
        if(!emBar.isJoinedToTransaction())      emBar.joinTransaction();
        Assert.isTrue(emBar.isJoinedToTransaction(),"没isJoinedToTransaction");

        Teacher teacher = teacherDao.save(topic);
         ///  teacherDao.flush();
        ///   emBar.merge(teacher);
        ///   emBar.flush();
    }
}



/*
    //private Specification<Qfjbxxdz> getWhereClause(final JSONArray condetion,final JSONArray search) {
        return new Specification<Qfjbxxdz>() {
            @Override
            public Predicate toPredicate(Root<Qfjbxxdz> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = new ArrayList<>();
                Iterator<JSONObject> iterator = condetion.iterator();
                Predicate preP = null;
                while (iterator.hasNext()) {
                    JSONObject jsonObject = iterator.next();
                    //注意：这里用的root.join 因为我们要用qfjbxx对象里的字段作为条件就必须这样做join方法有很多重载，使用的时候可以多根据自己业务决断
                    Predicate p1 = cb.equal(root.join("qfjbxx").get("id").as(String.class), jsonObject.get("fzId").toString());
                    Predicate p2 = cb.equal(root.get("fzcc").as(String.class), jsonObject.get("ccId").toString());
                    if (preP != null) {
                        preP = cb.or(preP, cb.and(p1, p2));
                    } else {
                        preP = cb.and(p1, p2);
                    }
                }
                JSONObject jsonSearch = (JSONObject) search.get(0);
                Predicate p3 = null;
                if (null != jsonSearch.get("xm") && jsonSearch.get("xm").toString().length() > 0) {
                    p3 = cb.like(root.join("criminalInfo").get("xm").as(String.class), "%" + jsonSearch.get("xm").toString() + "%");
                }
                Predicate p4 = null;
                if (null != jsonSearch.get("fzmc") && jsonSearch.get("fzmc").toString().length() > 0) {
                    p4 = cb.like(root.join("qfjbxx").get("fzmc").as(String.class), "%" + jsonSearch.get("fzmc").toString() + "%");
                }
                Predicate preA;
                if (null != p3 && null != p4) {
                    Predicate preS = cb.and(p3, p4);
                    preA = cb.and(preP, preS);
                } else if (null == p3 && null != p4) {
                    preA = cb.and(preP, p4);
                } else if (null != p3 && null == p4) {
                    preA = cb.and(preP, p3);
                } else {
                    preA = preP;
                }
                predicate.add(preA);
                Predicate[] pre = new Predicate[predicate.size()];
                query.where(predicate.toArray(pre));
                return query.getRestriction();
            }
        }
        */