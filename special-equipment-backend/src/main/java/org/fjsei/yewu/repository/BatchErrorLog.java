package org.fjsei.yewu.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//批量维护数据 中间例外情况处理表


@Getter
@Setter
@NoArgsConstructor
@Entity
public class BatchErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    private Long id;
    private Long oldId;     //对接旧系统ID

    private String name;    //关键标识
    private String error;   //原因

    private String addin;   //附带
    private Long sum;
    private String form;
    private String old;
    private String cmp;
    private String now;
}

