package org.fjsei.yewu.service.sdn;

import org.fjsei.yewu.controller.sdn.api.WeixingInput;
import org.fjsei.yewu.controller.sdn.api.WeixingOutput;
import org.fjsei.yewu.entity.incp.JcOpemen;
import org.fjsei.yewu.entity.incp.JcOpemenRepository;
import org.fjsei.yewu.entity.sdn.*;
import org.fjsei.yewu.repository.Teacher;
import org.fjsei.yewu.repository.TeacherDao;
import org.fjsei.yewu.repository.TbUntMge;
import org.fjsei.yewu.repository.TbUntMgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


@Service
public class WeixingServiceImpl implements WeixingService {

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TSdnEnpRepository tSdnEnpRepository;
    @Autowired
    private JcOpemenRepository jcOpemenRepository;
    @Autowired
    private TbUntMgeRepository tbUntMgeRepository;
    @Autowired
    private TEbmUserRepository tEbmUserRepository;


    @PersistenceContext(unitName = "entityManagerFactorySdn")
    private EntityManager emBar;


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

    @Transactional
    public Teacher getTeacher(String name) {
        Teacher topic=new Teacher("hua","22","bvvnn2");
        teacherDao.save(topic);
        return topic;
    }



    @Transactional
    public WeixingOutput accountBindSevice(WeixingInput topic) {
        if(!emBar.isJoinedToTransaction())      emBar.joinTransaction();
        WeixingOutput outObj= new WeixingOutput();
        if(topic.UNT_NAME.length()<3 || topic.WC_NUM.length()<3)	return outObj;

        TSdnEnp tSdnEnp=tSdnEnpRepository.findTopByUntNameAndTebmuser_LoginNameAndStatusIsNotLike(topic.UNT_NAME,topic.WC_NUM,"3");  //参数名称转换
        //接口返回该账户已经存在，无需重新绑定； 	b.USER_TYPE=2 AND a.user_id=? AND b.STATUS=1
        //&& tSdnEnp.getTEbmUser().getStatus().equals("0")
        if(tSdnEnp!=null ){
            outObj.returnCode="01";
            outObj.returnDesc="该账户已经绑定，无需重新绑定";
            return outObj.copyDataFromTSdnEnp(tSdnEnp,null,null);
        }

    //"select UNT_ID,UNT_ORG_COD,UNT_MOBILE,UNT_LKMEN from tb_unt_mge A where UNT_NAME=?"); 从检验平台的数据库查{分布式DB}
        TbUntMge tbUntMge=tbUntMgeRepository.findByUntName(topic.UNT_NAME);
        if(tbUntMge==null){
            outObj.returnCode="02";
            outObj.returnDesc="单位名称没找到，请确认";
            return outObj;
        }
        //继续看，估计是：这人没有微信账户。
        String newUSER_TYPE="";
        if(topic.UNT_MOBILE.equals(tbUntMge.getUntMobile()) ) //从检验平台看：他是管理员帐户
        {
            tSdnEnpRepository.closeManagerAcountOfUnt(topic.UNT_NAME);
            // tSdnEnpRepository.findAll(new Specification<TSdnEnp> () )
            //从检验平台看：这人是管理员帐户. ?已经存在管理员帐户，如果存在，直接把该管理员帐户注销。
            tEbmUserRepository.closeManagerAcountOfUnt(topic.UNT_NAME);
            newUSER_TYPE = "1";
            //把该账户写入到报检平台用户表，且设置为管理员帐户，
        }else {
            //他不是管理员
            //报检平台存在管理员帐户
            //select a.user_id,b.LOGIN_NAME,a.LKMAN_MOBIL from T_SDN_ENP a,T_EBM_USER b WHERE a.user_id=b.user_id AND b.status=0 AND b.USER_TYPE=1 AND a.unt_name=?"
            if (tEbmUserRepository.existsByStatusAndUserTypeAndTsdnenp_UntName("0", "1", topic.UNT_NAME)) {
                newUSER_TYPE = "2";
                //如果存在，直接在报检平台用户表中插入一条待审核的用户记录，
            }else{
                //他不是管理员 && 单位的管理员帐户不存在，接口返回单位名称或管理员手机号错误
                outObj.returnCode="02";
                outObj.returnDesc="单位的管理员帐户不存在，请确认";
                return outObj;
            }
        }
        ///if(!newUSER_TYPE.equals(""))
            TEbmUser tEbmUser=new TEbmUser();
            tEbmUser.setUserName(topic.UNT_LKMEN);
            tEbmUser.setPassword("123456");
            tEbmUser.setLoginName(topic.WC_NUM);
            tEbmUser.setStatus("1");
            tEbmUser.setUserType(newUSER_TYPE);
            TSdnEnp tSdnEnp2=new TSdnEnp();
            tSdnEnp2.setTebmuser(tEbmUser);
            tSdnEnp2.setUntName(topic.UNT_NAME);
            tSdnEnp2.setStatus("1");
            tSdnEnp2.setUntCod(tbUntMge.getUntId());
            tSdnEnp2.setUntLkman(tbUntMge.getUntLkmen());
            tSdnEnp2.setLkmanMobil(tbUntMge.getUntMobile());
            tSdnEnp2.setCertSerial("");     //数据库目前是必填的
            java.util.Date  now = new java.util.Date();
            tSdnEnp2.setCreateDate(new java.sql.Date(now.getTime()));
            tSdnEnpRepository.save(tSdnEnp2);

        if(newUSER_TYPE.equals("1")) {
            outObj.returnCode = "00";
            outObj.returnDesc = "绑定成功";
        }
        else{
            outObj.returnCode = "03";
            outObj.returnDesc = "帐户绑定申请成功，请联系单位管理员审核";
        }
        return outObj.copyDataFromTSdnEnp(tSdnEnp2,tbUntMge.getUntId(),tbUntMge.getUntOrgCod());
    }

   // @Transactional
    public WeixingOutput opeMenBindSevice(String name, String idCard, String mobile)
    {
        //if(!emBar.isJoinedToTransaction())      emBar.joinTransaction();
        JcOpemen jcOpemen=jcOpemenRepository.findByNameAndIdCardAndMobile(name,idCard,mobile);
        WeixingOutput outObj= new WeixingOutput();
        if(jcOpemen!=null){ 	//接口返回该账户已经存在，无需重新绑定；
            outObj.returnCode="00";
            outObj.returnDesc="认证成功";
        }
        else
        {
            outObj.returnCode="01";
            outObj.returnDesc="认证失败，不存在该作业人员，请核实信息";
        }
        return outObj;
    }


    @Transactional
    public WeixingOutput chkUntAccount(WeixingInput topic) {
        if(!emBar.isJoinedToTransaction())      emBar.joinTransaction();
        WeixingOutput outObj= new WeixingOutput();
        if(!topic.STATUS.equals("0") && !topic.STATUS.equals("3"))	return outObj;
   //     TSdnEnp tSdnEnp=tSdnEnpRepository.findByUserId(topic.USER_ID);
        TSdnEnp tSdnEnp=tSdnEnpRepository.findByTebmuser_userId(topic.USER_ID);
        //接口返回该账户已经存在，无需重新绑定； 	b.USER_TYPE=2 AND a.user_id=? AND b.STATUS=1
        if(tSdnEnp!=null && tSdnEnp.getTebmuser().getStatus().equals("1") && tSdnEnp.getTebmuser().getUserType().equals("2")){
        }
        else {
            outObj.returnDesc = "没该账户在申请";
            return outObj;
        }
        tSdnEnp.getTebmuser().setStatus(topic.STATUS);
        tSdnEnp.getTebmuser().setUserType("2");
        tSdnEnpRepository.save(tSdnEnp);
        if(topic.STATUS.equals("0")){
            outObj.returnCode="00";
            outObj.returnDesc="审核通过";
        }
        else //if(STATUS.equals("3"))
        {
            outObj.returnCode="01";
            outObj.returnDesc="审核不通过";
        }
        return outObj;
    }

}


