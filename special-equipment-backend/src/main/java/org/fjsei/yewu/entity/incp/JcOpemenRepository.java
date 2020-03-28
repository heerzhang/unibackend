package org.fjsei.yewu.entity.incp;

import org.springframework.data.repository.CrudRepository;


//可根据接口函数名字自动生成HQL的模式。

public interface JcOpemenRepository extends CrudRepository<JcOpemen, String> {
    JcOpemen findByUserId(String userId);
    ///"select USER_ID from JC_OPEMEN  WHERE NAME=? AND ID_CARD=? AND MOBILE=?"
    JcOpemen findByNameAndIdCardAndMobile(String name, String idCard, String mobile);
}

