package org.fjsei.yewu.service.core;

import md.specialEqp.Eqp;
import md.specialEqp.EqpRepository;
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



@Service
public class RestServiceImpl implements RestService {

    @Autowired
    private EqpRepository eqpRepository;
    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emBar;

    @Override
    public Page<Eqp> findByName_Page(String name) {
        //根据id 进行降序
        Sort.Order order =  new Sort.Order(Sort.Direction.DESC,"id");
        return null;
    }

    @Override
    public Eqp findByName(String name) {
        Eqp eqp1=eqpRepository.findByCod(name);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith()) //姓名采用“开始匹配”的方式查询
                .withIgnorePaths("focus");  //忽略属性：是否关注。因为是基本类型，需要忽略掉
        //创建实例
        Example<Eqp> ex = Example.of(eqp1, matcher);
        //查询      ? id+ ! X对一关联表 字段也会算比较条件
        List<Eqp> ls = eqpRepository.findAll(ex);
        //输出结果
        System.out.println("数量："+ls.size());
        for (Eqp bo:ls)
        {
            System.out.println(bo.getCod());
        }
        return null;//employeeList;
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
    }
}


