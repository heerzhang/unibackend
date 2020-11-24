package org.fjsei.yewu.resolver.sei;

import com.querydsl.core.BooleanBuilder;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import md.cm.base.*;
import md.cm.geography.*;
import md.cm.unit.QUnit;
import md.cm.unit.Unit;
import md.cm.unit.UnitRepository;
import md.specialEqp.*;
import md.specialEqp.inspect.ISPRepository;
import md.specialEqp.inspect.TaskRepository;
import md.specialEqp.type.*;
import md.system.AuthorityRepository;
import md.system.UserRepository;
import org.elasticsearch.action.support.WriteRequest;
import org.fjsei.yewu.entity.fjtj.*;
import org.fjsei.yewu.index.sei.*;
import org.fjsei.yewu.jpa.PageOffsetFirst;
import org.fjsei.yewu.security.JwtTokenUtil;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.*;

//这个类名字不能重复简明！
//后台维护!

@Slf4j
@Component
public class MaintenanceMutation implements GraphQLMutationResolver {
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
    @Autowired private EqpMgeRepository eqpMgeRepository;
    @Autowired private SomeEsRepository someEsRepository;
    @Autowired
    private ElasticsearchRestTemplate esTemplate;
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
                //todo:不能保证唯一性，？以UNT_ID大的为准
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

    //仅先从老旧平台倒腾到eqp实体类，相关的ES部分另外再搞；
    @Transactional(rollbackFor = Exception.class)
    public Iterable<String> syncEqpFromOld(int offset, int limit) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        //分片任务，保证可以重复执行，确保findAll读取出来的记录有顺序。
        Iterable<EqpMge> untMges= eqpMgeRepository.findAll(pageable);
        List<String> retMsgs=new ArrayList<>();
        for (EqpMge each:untMges)
        {
            BooleanBuilder builder= new BooleanBuilder();;
            QEqp qm2 = QEqp.eqp;
            if (each.getOIDNO()!=null)
                builder.and(qm2.oid.eq(each.getOIDNO()));
            if (each.getEqpcod()!=null)
                builder.and(qm2.cod.eq(each.getEqpcod()));
            if (each.getFACTORY_COD()!=null)
                builder.and(qm2.fNo.eq(each.getFACTORY_COD()));
            if (each.getEQP_USECERT_COD()!=null)
                builder.and(qm2.cert.eq(each.getEQP_USECERT_COD()));
            if (each.getREG_UNT_ID()!=null)
                builder.and(qm2.regU.oldId.eq(each.getREG_UNT_ID()));
            //确保设备不重复！
            Eqp eqp;
            if(eQPRepository.exists(builder)) {
                retMsgs.add("成功");
                continue;
            }
            eqp=Eqp.builder().oid(each.getOIDNO()).cod(each.getEqpcod()).type(each.getEQP_TYPE()).sort(each.getEQP_SORT()).vart(each.getEQP_VART()).subVart(each.getSUB_EQP_VART())
                .reg(each.getEQP_REG_STA()).ust(each.getEQP_USE_STA()).cag(each.getIN_CAG()).cert(each.getEQP_USECERT_COD()).sNo(each.getEQP_STATION_COD())
                .rcod(each.getEQP_REG_COD()).level(each.getEQP_LEVEL()).fNo(each.getFACTORY_COD()).name(each.getEQP_NAME()).plNo(each.getEQP_INNER_COD()).model(each.getEQP_MOD())
                .cping(each.getIF_INCPING()=='1').important(each.getIF_MAJEQP()!=null&&( each.getIF_MAJEQP().equals("1")||each.getIF_MAJEQP().equals("是")) )
                    .useDt(each.getFIRSTUSE_DATE())
                .accpDt(each.getCOMPE_ACCP_DATE()).expire(each.getDESIGN_USE_OVERYEAR())
                    .move(each.getIS_MOVEEQP()!=null&&each.getIS_MOVEEQP()=='1').area(each.getEQP_AREA_COD()).addr(each.getEQP_USE_ADDR())
                .occasion(each.getEQP_USE_OCCA()).buildId(each.getBUILD_ID())
                    .ePrice(each.getEQP_PRICE()!=null?each.getEQP_PRICE():0).contact(each.getUSE_MOBILE())
                    .unqf1(each.getNOTELIGIBLE_FALG1()).unqf2(each.getNOTELIGIBLE_FALG2())
                    .ccl1(each.getLAST_ISP_CONCLU1()).ccl2(each.getLAST_ISP_CONCLU2())
                    .ispD1(each.getLAST_ISP_DATE1()).ispD2(each.getLAST_ISP_DATE2()).nxtD1(each.getNEXT_ISP_DATE1())
                .nxtD2(each.getNEXT_ISP_DATE2()).build();
            QUnit qm = QUnit.unit;
            Unit unit;

            if(each.getPROP_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getPROP_UNT_ID()));
                //确保只能最多找到一个单位！
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setOwner(unit);
            }
            //todo:使用单位反而都有的，   产权人却是可省略？
            if(each.getREG_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getREG_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setRegU(unit);
            }
            if(each.getUSE_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getUSE_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setUseU(unit);
            }
            if(each.getMANT_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getMANT_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setMtU(unit);
            }
            if(each.getMAKE_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getMAKE_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setMakeU(unit);
            }
            if(each.getINST_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getINST_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setInsU(unit);
            }
            if(each.getALT_UNT_ID()!=null) {
                builder = new BooleanBuilder();
                builder.and(qm.oldId.eq(each.getALT_UNT_ID()));
                unit = unitRepository.findOne(builder).orElse(null);
                eqp.setRemU(unit);
            }
            Eqp targ;
            if(each.getEQP_TYPE().equals("3000"))
                targ=new Elevator();
            else if(each.getEQP_TYPE().equals("4000"))
                targ=new Crane();
            else if(each.getEQP_TYPE().equals("2000"))
                targ=new Vessel();
            else if(each.getEQP_TYPE().equals("8000"))
                targ=new Pipeline();
            else if(each.getEQP_TYPE().equals("1000"))
                targ=new Boiler();
            else if(each.getEQP_TYPE().equals("5000"))
                targ=new FactoryVehicle();
            else if(each.getEQP_TYPE().equals("6000"))
                targ=new Amusement();
            else
                targ=new Eqp();
            BeanUtils.copyProperties(eqp,targ);
            eQPRepository.save(targ);
            retMsgs.add("成功");
        }
        log.info("syncEqpFromOld:{}", offset);
        return retMsgs;
    }

    //从Eqp以及关联实体提取数据腾挪到EqpEs索引中去
    @Transactional(rollbackFor = Exception.class)
    public Iterable<String> syncEqpEsFromEqp_正常维护代码保留(int offset, int limit) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        Iterable<Eqp> eqps= eQPRepository.findAll(pageable);
        List<String> retMsgs=new ArrayList<>();
        for (Eqp eqp:eqps)
        {
            EqpEs eqpEs=eqpEsRepository.findById(eqp.getId()).orElse(null);
            if(null!=eqpEs){
                //已经有了，修改。
                retMsgs.add("成功");
            }else{
                EqpEs one=new EqpEs();
                BeanUtils.copyProperties(eqp,one);
                //可直接替换旧的; 若事务失败了,ES也无法回退，造成ES记录重复但是id实际不在DB中的。
                eqpEsRepository.save(one);
                retMsgs.add("成功");
            }
        }
        log.info("syncEqpEsFromEqp:{}", offset);
        return retMsgs;
    }
    //测试看得　代码　　速度=0.48秒/条
    public Iterable<String> syncEqpEsFromEqp_单向测试(int offset, int limit) {
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        Iterable<Eqp> fromeqps= eQPRepository.findAll(pageable);
        List<String> retMsgs=new ArrayList<>();
        for (Eqp eqpfrom:fromeqps)
        {
            EqpEs eqp=eqpEsRepository.findById(330660L).orElse(null);
            if(null!=eqpfrom){
                Unit  useU=eqpfrom.getUseU();
                if(null!=useU) {
                    UnitEs unitEs = new UnitEs();
                    unitEs.setId(129202L);
                    unitEs.setName("刘必腾");
                    unitEs.setAddress("福建省连江县透堡镇龙头村龙峰街10号(流动作业设备)");
                    eqp.setUseU(unitEs);
                }

                eqpEsRepository.save(eqp);
            }
            retMsgs.add("成功");
        }
        log.info("syncEqpEsFromEqp:{}", offset);
        return retMsgs;
    }
    //_正常用的保留 速度=0.0047秒/条，比非批量更新的可快百倍
    public Iterable<String> syncEqpEsFromEqp(int offset, int limit) {
        Pageable pageable= PageOffsetFirst.of(offset, limit);
        Iterable<Eqp> fromeqps= eQPRepository.findAll(pageable);
        List<String> retMsgs=new ArrayList<>();
        List<EqpEs> neweqpes=new ArrayList<>();
        //3个看
        for (Eqp eqpfrom:fromeqps)
        {
            EqpEs eqp=eqpEsRepository.findById(eqpfrom.getId()).orElse(null);
            if(null!=eqpfrom){
                Map<String, Object> params = new HashMap<>();
                Unit  useU=eqpfrom.getUseU();
                if(null!=useU) {
                    UnitEs unitEs = new UnitEs();
                    unitEs.setId(useU.getId());
                    unitEs.setName(null!=useU.getCompany()? useU.getCompany().getName():useU.getPerson().getName());
                    unitEs.setAddress(null!=useU.getCompany()? useU.getCompany().getAddress():useU.getPerson().getAddress());
                    eqp.setUseU(unitEs);
                }
                useU=eqpfrom.getOwner();
                if(null!=useU) {
                    UnitEs unitEs = new UnitEs();
                    unitEs.setId(useU.getId());
                    unitEs.setName(null!=useU.getCompany()? useU.getCompany().getName():useU.getPerson().getName());
                    unitEs.setAddress(null!=useU.getCompany()? useU.getCompany().getAddress():useU.getPerson().getAddress());
                    eqp.setOwner(unitEs);
                }
                neweqpes.add(eqp);
            }
            retMsgs.add("成功");
        }
        eqpEsRepository.saveAll(neweqpes);
        log.info("syncEqpEsFromEqp:{}", offset);
        return retMsgs;
    }
}


