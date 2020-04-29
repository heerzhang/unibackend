package org.fjsei.yewu.service.sdn;

import org.fjsei.yewu.controller.sdn.api.WeixingInput;
import org.fjsei.yewu.controller.sdn.api.WeixingOutput;
import org.fjsei.yewu.entity.sdn.Student;
import org.fjsei.yewu.repository.Teacher;

import java.util.List;


//不是正常的entity实体的接口，必须通过附加的转换适配器来做输入输出的对象处理 。

public interface WeixingService {

    Student findByName(String name);
    Teacher getTeacher(String name);
    List<Teacher> getAllTeacher();

    public WeixingOutput accountBindSevice(WeixingInput topic);
    public WeixingOutput opeMenBindSevice(String name, String idCard, String mobile);
    public WeixingOutput chkUntAccount(WeixingInput topic);

}


