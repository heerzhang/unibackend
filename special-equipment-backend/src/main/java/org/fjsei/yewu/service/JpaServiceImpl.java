package org.fjsei.yewu.service;

import org.fjsei.yewu.repository.Teacher;
import org.fjsei.yewu.repository.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.*;


/*
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 11:13
 */
//@Transactional(value="transactionManager",readOnly=true)

@Service
//@Qualifier("entityManagerFactoryBar")
@Transactional(readOnly=true)
public class JpaServiceImpl implements JpaService {
    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;


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


