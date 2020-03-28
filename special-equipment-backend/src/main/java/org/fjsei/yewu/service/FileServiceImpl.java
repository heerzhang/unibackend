package org.fjsei.yewu.service;

import org.fjsei.yewu.entity.sei.File;
import org.fjsei.yewu.entity.sei.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 10:07
 **/

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emBar;

    @Transactional
    public String addFile(String url) {
        if(!emBar.isJoinedToTransaction())      System.out.println("没达到 em.isJoinedToTransaction()");
        else System.out.println("到 em.isJoinedToTransaction()");
        if(!emBar.isJoinedToTransaction())      emBar.joinTransaction();

        File file=new File();
        file.setUrl(url);
        fileRepository.save(file);
        ///  teacherDao.flush();
        ///   emBar.merge(teacher);
        ///   emBar.flush();
        return file.getId().toString();
    }

    //获取某ID的文件真实路径名。
    public String getFileOfID(String sid) {
        Long    id;
        try {
            id=Long.parseLong(sid);
        } catch (Exception ex) {
            return null;
        }
        File file=fileRepository.findById(id).orElse(null);
        String  fileName=null;
        if(file!=null) {
            fileName=file.getUrl();
        }
        return fileName;
    }
}

