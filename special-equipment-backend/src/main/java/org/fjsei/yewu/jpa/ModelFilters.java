package org.fjsei.yewu.jpa;

import org.fjsei.yewu.input.WhereTree;
import org.springframework.data.jpa.domain.Specification;

//JPA的子查询只能支持1个列的输出。
//特别注意：接口安全性灵活性的平衡，此处的安全机制还需要进一步研究和落实。


public interface ModelFilters<T>  extends Specification<T> {

    //通过接口来　支持WhereTree输入条件。
    ModelFilters<T> effectWhereTree(WhereTree where);

    //支持限制最大返回记录的数量
    ModelFilters<T> effectCount(int rownum);

   //[安全考虑]限制subquery可以使用的模型类type?。

    // 接口签名授权？

}




