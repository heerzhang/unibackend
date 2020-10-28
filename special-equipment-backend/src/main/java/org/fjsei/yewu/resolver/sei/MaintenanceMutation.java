package org.fjsei.yewu.resolver.sei;

import com.alibaba.fastjson.JSON;
import com.querydsl.core.BooleanBuilder;
import graphql.kickstart.tools.GraphQLMutationResolver;
import md.cm.base.*;
import md.cm.geography.*;
import md.cm.unit.Unit;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISP;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.Task;
import md.specialEqp.inspect.TaskRepository;
import md.specialEqp.type.Elevator;
import md.specialEqp.type.ElevatorRepository;
import md.system.AuthorityRepository;
import md.system.User;
import md.system.UserRepository;
import org.fjsei.yewu.entity.fjtj.HrUserinfo;
import org.fjsei.yewu.entity.fjtj.HrUserinfoRepository;
import org.fjsei.yewu.entity.fjtj.UntMge;
import org.fjsei.yewu.entity.fjtj.UntMgeRepository;
import org.fjsei.yewu.exception.BookNotFoundException;
import org.fjsei.yewu.index.sei.*;
import org.fjsei.yewu.input.DeviceCommonInput;
import org.fjsei.yewu.input.UnitCommonInput;
import org.fjsei.yewu.jpa.PageOffsetFirst;
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

//这个类名字不能重复简明！
//后台维护

@Component
public class MaintenanceMutation implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private EQPRepository eQPRepository;
    @Autowired
    private EqpEsRepository eqpEsRepository;
    @Autowired
    private ElevatorRepository elevatorRepository;
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
    @Autowired private UntMgeRepository untMgeRepository;

    //仅用于后台维护使用的；
    @Transactional(rollbackFor = Exception.class)
    public Iterable<String> syncUnitFromOld(int offset, int limit) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        Iterable<UntMge> untMges= untMgeRepository.findAll(pageable);
        List<String> retMsgs=new ArrayList<>();
        for (UntMge untMge:untMges)
        {
            if(untMge.getUNT_NAME().length()<=0){
                retMsgs.add("name null offset="+offset);
                continue;
            }
            boolean isPerson= "Z01".equals(untMge.getINDUSTRY_PROP_COD()) || untMge.getUNT_NAME().length()<=3;
            if(!isPerson)
            {
                QCompany qm = QCompany.company;
                BooleanBuilder builder = new BooleanBuilder();
                builder.and(qm.name.eq(untMge.getUNT_NAME()));
                if (untMge.getUNT_ORG_COD()!=null)
                    builder.and(qm.no.eq(untMge.getUNT_ORG_COD()));
                Company company= companyRepository.findOne(builder).orElse(null);
                if(null!=company){
                    //依照老旧平台来比较修改。
                    company.copyAs(untMge);
                    try {
                        //如果没有实际被修改就不会真的做SQL命令。
                        companyRepository.saveAndFlush(company);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        retMsgs.add(e.getMessage()+",offset="+offset);
                        continue;
                    }
                    CompanyEs companyEs=new CompanyEs();
                    BeanUtils.copyProperties(company,companyEs);
                    companyEsRepository.save(companyEs);
                    retMsgs.add("成功");
                }else{
                    //BeanUtils.copyProperties(untMge,company);
                    Company company2=Company.builder().address(untMge.getUNT_ADDR()).linkMen(untMge.getUNT_LKMEN()).name(untMge.getUNT_NAME())
                            .no(untMge.getUNT_ORG_COD()).phone(untMge.getUNT_MOBILE()).build();
                    Unit unit=Unit.builder().indCod(untMge.getINDUSTRY_PROP_COD()).oldId(untMge.getId()).jcId(untMge.getJC_UNT_ID())
                            .company(company2).build();
                    try {
                        companyRepository.saveAndFlush(company2);
                        unitRepository.saveAndFlush(unit);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        retMsgs.add(e.getMessage()+",offset="+offset);
                        continue;
                    }
                    CompanyEs companyEs=new CompanyEs();
                    BeanUtils.copyProperties(company2,companyEs);
                    //可直接替换旧的; 若事务失败了,ES也无法回退，造成ES记录重复但是id实际不在DB中的。
                    companyEsRepository.save(companyEs);
                    retMsgs.add("成功");
                }
            }
            else{
                QPerson qm = QPerson.person;
                BooleanBuilder builder = new BooleanBuilder();
                builder.and(qm.name.eq(untMge.getUNT_NAME()));
                if (untMge.getUNT_ORG_COD()!=null)
                    builder.and(qm.no.eq(untMge.getUNT_ORG_COD()));
                Person company= personRepository.findOne(builder).orElse(null);
                if(null!=company){
                    company.copyAs(untMge);
                    try {
                        //如果没有实际被修改就不会真的做SQL命令。
                        personRepository.saveAndFlush(company);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        retMsgs.add(e.getMessage()+",offset="+offset);
                        continue;
                    }
                    PersonEs companyEs=new PersonEs();
                    BeanUtils.copyProperties(company,companyEs);
                    personEsRepository.save(companyEs);
                    retMsgs.add("成功");
                }else{
                    Person company2=Person.builder().address(untMge.getUNT_ADDR()).name(untMge.getUNT_NAME())
                            .no(untMge.getUNT_ORG_COD()).phone(untMge.getUNT_MOBILE()).build();
                    Unit unit=Unit.builder().indCod(untMge.getINDUSTRY_PROP_COD()).oldId(untMge.getId()).jcId(untMge.getJC_UNT_ID())
                            .person(company2).build();
                    try {
                        personRepository.saveAndFlush(company2);
                        unitRepository.saveAndFlush(unit);
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        retMsgs.add(e.getMessage()+",offset="+offset);
                        continue;
                    }
                    PersonEs companyEs=new PersonEs();
                    BeanUtils.copyProperties(company2,companyEs);
                    personEsRepository.save(companyEs);
                    retMsgs.add("成功");
                }
            }
        }
        return retMsgs;
    }

}


