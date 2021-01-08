package org.fjsei.yewu.resolver.sei.oldsys;

import graphql.kickstart.tools.GraphQLQueryResolver;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import org.fjsei.yewu.entity.fjtj.*;
import md.specialEqp.inspect.IspRepository;
import md.specialEqp.inspect.TaskRepository;
import md.cm.geography.AddressRepository;
import org.fjsei.yewu.security.JwtUser;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Component
public class SysMgrQuery implements GraphQLQueryResolver {

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
    @Autowired
    private EqpMgeRepository eqpMgeRepository;
    @Autowired
    private ElevParaRepository elevParaRepository;
    @Autowired
    private HouseMgeRepository houseMgeRepository;
    @Autowired
    private UntMgeRepository untMgeRepository;


    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;

    private User checkAuth() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null)  return null;
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

    public EqpMge findEqpMge(Long id) {
        User user= checkAuth();
        if(user==null)   return null;
        //Pageable pageable =PageRequest.of(1,100);
         // List<EqpMge> eqps= eqpMgeRepository.findAllByEQPCODIsLike("%958%");
        EqpMge eqp=eqpMgeRepository.findById(id).orElse(null);

        ElevPara elevPara=elevParaRepository.getByEqpcodEquals(eqp.getEqpcod());
       // Long build_id= eqp.getBUILD_ID();
        //HouseMge houseMge=houseMgeRepository.getOne(5194L); 不可使用getOne，未加载的实体记录就查不到。
        HouseMge houseMge=houseMgeRepository.findById(eqp.getBUILD_ID()).orElse(null);
        UntMge untMge=untMgeRepository.findById(eqp.getUSE_UNT_ID()).orElse(null);

        return eqp;
    }

}


/*
graphql需要参数的接口函数/Type输出isp(带参数)的/等，注意！参数不能直接用POJO的java类来传递参数对象，需要基本数据类型或其嵌套结构；否则报错：
输出可以用java对象，输入却免谈． graphql.kickstart.tools.SchemaError: Expected type 'Isp' to be a GraphQLInputType, but it wasn't!
 Was a type only permitted for object types incorrectly used as an input type, or vice-versa? at graphql.kickstart.tools.SchemaParser.d。
*/
