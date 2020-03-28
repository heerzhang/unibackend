package org.fjsei.yewu.resolver.sei.original;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.fjsei.yewu.entity.sei.*;
import org.fjsei.yewu.entity.sei.inspect.ISPRepository;
import org.fjsei.yewu.entity.sei.inspect.TaskRepository;
import org.fjsei.yewu.exception.BookNotFoundException;
import org.fjsei.yewu.model.geography.AddressRepository;
import org.fjsei.yewu.security.JwtTokenUtil;
import org.fjsei.yewu.service.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Component
public class ReportMgrMutation implements GraphQLMutationResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    private FileRepository fileRepository;


    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;                //EntityManager相当于hibernate.Session：

    @Autowired
    private final JwtTokenUtil jwtTokenUtil=new JwtTokenUtil();

    /*
@Transactional
public OriginalRecord newOriginalRecord(String modeltype, String modelversion, Long ispId, String data) {
    if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();

    ISP isp = iSPRepository.findById(ispId).orElse(null);
    if(isp == null)     throw new BookNotFoundException("没有该ISP", ispId);

    OriginalRecord originalRecord = new OriginalRecord(modeltype,modelversion,isp,data);
    originalRecordRepository.save(originalRecord);
    return originalRecord;
}
    */

    @Transactional
    public Report modifyOriginalRecordFiles(Long id, List<Long> fileIDs) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Report originalRecord= reportRepository.findById(id).orElse(null);
        if(originalRecord == null)     throw new BookNotFoundException("没有该原始记录", id);
        fileIDs.stream().forEach(item -> {
            File file=fileRepository.findById(item).orElse(null);
            file.getUrl();
        });
        originalRecord.setFiles(null);

        reportRepository.save(originalRecord);
        return originalRecord;
    }
    @Transactional
    public Report modifyOriginalRecordData(Long id, int operationType, String data, String deduction) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Report originalRecord= reportRepository.findById(id).orElse(null);
        if(originalRecord == null)     throw new BookNotFoundException("没有该原始记录", id);
        if(1==operationType)
            originalRecord.setData(data);
        else if(2==operationType){
            //权限验证
        }
        reportRepository.save(originalRecord);
        return originalRecord;
    }

}
