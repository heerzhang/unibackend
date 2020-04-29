package org.fjsei.yewu.resolver.sei.original;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.fjsei.yewu.entity.sei.*;
import org.fjsei.yewu.entity.sei.inspect.ISPRepository;
import org.fjsei.yewu.entity.sei.inspect.TaskRepository;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Component
public class ReportMgrQuery implements GraphQLQueryResolver {

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


    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;




}


/*
graphql需要参数的接口函数/Type输出isp(带参数)的/等，注意！参数不能直接用POJO的java类来传递参数对象，需要基本数据类型或其嵌套结构；否则报错：
输出可以用java对象，输入却免谈． graphql.kickstart.tools.SchemaError: Expected type 'ISP' to be a GraphQLInputType, but it wasn't!
 Was a type only permitted for object types incorrectly used as an input type, or vice-versa? at graphql.kickstart.tools.SchemaParser.d。
*/
